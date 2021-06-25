#![forbid(unsafe_code)]
#![deny(rust_2018_idioms, clippy::all, clippy::pedantic)]
#![warn(clippy::nursery)]
#![allow(
    dead_code,
    unused_imports,
    clippy::missing_errors_doc,
    clippy::module_name_repetitions
)]

use std::{
    borrow::Cow,
    fs::{self, File, OpenOptions},
    io::{prelude::*, BufReader, BufWriter},
    path::{Path, PathBuf},
};

use chrono::{prelude::*, Duration};
use serde::{Deserialize, Serialize};

use crate::{
    errors::Result,
    history::{HistoryRepository, JsonHistoryRepository},
    intake_size::{IntakeSizeRepository, TomlIntakeSizeRepository},
    models::{IntakeSize, Record, Schedule},
    schedule::{ScheduleRepository, TomlScheduleRepository},
};

pub mod errors;
pub mod history;
pub mod intake_size;
pub mod models;
pub mod schedule;

pub type DrinkUpDefault =
    DrinkUp<TomlScheduleRepository, JsonHistoryRepository, TomlIntakeSizeRepository>;

pub struct DrinkUp<SR, HR, ISR> {
    schedule_repo: SR,
    history_repo: HR,
    intake_size_repo: ISR,

    schedule: Schedule,
    intake_sizes: Vec<IntakeSize>,
    today: Date<Utc>,
    today_history: Vec<Record>,
}

impl<SR, HR, ISR> DrinkUp<SR, HR, ISR>
where
    SR: ScheduleRepository,
    HR: HistoryRepository,
    ISR: IntakeSizeRepository,
{
    pub fn new(schedule_repo: SR, history_repo: HR, intake_size_repo: ISR) -> Result<Self> {
        let schedule = schedule_repo.load()?;
        let intake_sizes = intake_size_repo.list()?;
        let today = Utc::now().date();
        let today_history = history_repo.load(today)?;

        Ok(Self {
            schedule_repo,
            history_repo,
            intake_size_repo,
            schedule,
            intake_sizes,
            today,
            today_history,
        })
    }

    pub fn schedule(&self) -> &Schedule {
        &self.schedule
    }

    pub fn save_schedule(&mut self, schedule: &Schedule) -> Result<()> {
        self.schedule = schedule.clone();
        self.schedule_repo.save(schedule)
    }

    pub fn intake_sizes(&self) -> &[IntakeSize] {
        &self.intake_sizes
    }

    pub fn save_intake_size(&mut self, intake_size: &IntakeSize) -> Result<()> {
        let id = self.intake_size_repo.save(intake_size)?;

        if let Some(size) = self
            .intake_sizes
            .iter_mut()
            .find(|is| is.id == intake_size.id)
        {
            *size = intake_size.clone();
        } else {
            let mut clone = intake_size.clone();
            clone.id = id;
            self.intake_sizes.push(clone);
        }

        Ok(())
    }

    pub fn history(&mut self, date: Date<Utc>) -> Result<Cow<'_, [Record]>> {
        self.update_today()?;

        if self.today == date {
            Ok(Cow::from(&self.today_history))
        } else {
            self.history_repo.load(date).map(Cow::from)
        }
    }

    pub fn add_record(&mut self, record: &Record) -> Result<()> {
        self.update_today()?;
        self.today_history.push(record.clone());
        self.history_repo.save(record)
    }

    pub fn progress(&mut self) -> Result<u32> {
        let today = Utc::now().date();
        if self.today != today {
            self.today_history = self.history_repo.load(today)?;
            self.today = today;
        }

        Ok(self.today_history.iter().map(|r| r.amount).sum())
    }

    fn update_today(&mut self) -> Result<()> {
        let today = Utc::now().date();
        if self.today != today {
            self.today_history = self.history_repo.load(today)?;
            self.today = today;
        }
        Ok(())
    }

    pub fn next_alarm(&self) -> Result<Option<u32>> {
        let schedule = &self.schedule;
        let now = Local::now().time();

        // The schedule is outside of the active time. Start and end time can be inversed as well.
        if schedule.start < schedule.end && (now < schedule.start || schedule.end < now)
            || schedule.start > schedule.end && (now > schedule.start || schedule.end > now)
        {
            return Ok(None);
        }

        Ok(Some(30 - now.minute() % 30))
    }

    #[allow(clippy::cast_possible_truncation, clippy::cast_sign_loss)]
    pub fn drink_amount(&mut self) -> Result<u32> {
        let remaining = self.schedule.goal - self.progress()?;
        let total_hours = (self.schedule.end - self.schedule.start).num_hours().abs() as u32;

        Ok(remaining / total_hours / 2)
    }
}

impl DrinkUp<TomlScheduleRepository, JsonHistoryRepository, TomlIntakeSizeRepository> {
    pub fn new_default(base_path: impl AsRef<Path>) -> Result<Self> {
        let base_path = base_path.as_ref();
        Self::new(
            schedule::new_repo(base_path),
            history::new_repo(base_path),
            intake_size::new_repo(base_path),
        )
    }
}

#[cfg(test)]
mod tests {
    use std::ops::Add;

    use chrono::Duration;

    use super::*;
    use crate::{
        history::tests::FakeHistoryRepository, intake_size::tests::FakeIntakeSizeRepository,
        schedule::tests::FakeScheduleRepository,
    };

    type DrinkUpFake =
        DrinkUp<FakeScheduleRepository, FakeHistoryRepository, FakeIntakeSizeRepository>;

    fn new_fake() -> Result<DrinkUpFake> {
        DrinkUp::new(
            FakeScheduleRepository::new(Schedule {
                start: NaiveTime::from_hms(8, 0, 0),
                end: NaiveTime::from_hms(22, 0, 0),
                goal: 2400,
            }),
            FakeHistoryRepository::new(vec![Record {
                id: 1,
                timestamp: Utc::now().date().and_hms(0, 0, 0),
                name: "Test".to_owned(),
                amount: 5,
            }]),
            FakeIntakeSizeRepository::new(vec![IntakeSize {
                id: 1,
                name: "Test".to_owned(),
                amount: 5,
            }]),
        )
    }

    #[test]
    fn new_empty() {
        let dir = tempfile::tempdir().unwrap();
        DrinkUp::new_default(dir.path()).unwrap();
    }

    #[test]
    fn get_schedule() {
        let drinkup = new_fake().unwrap();

        let expect = Schedule {
            start: NaiveTime::from_hms(8, 0, 0),
            end: NaiveTime::from_hms(22, 0, 0),
            goal: 2400,
        };

        assert_eq!(&expect, drinkup.schedule());
        assert_eq!(expect, drinkup.schedule);
    }

    #[test]
    fn save_schedule() {
        let mut drinkup = new_fake().unwrap();
        let schedule = Schedule {
            start: NaiveTime::from_hms(1, 0, 0),
            end: NaiveTime::from_hms(2, 0, 0),
            goal: 3,
        };

        drinkup.save_schedule(&schedule).unwrap();
        let got = drinkup.schedule();

        assert_eq!(&schedule, got);
        assert_eq!(schedule, drinkup.schedule);
    }

    #[test]
    fn get_intake_sizes() {
        let drinkup = new_fake().unwrap();

        let expect = &[IntakeSize {
            id: 1,
            name: "Test".to_owned(),
            amount: 5,
        }];

        assert_eq!(expect, drinkup.intake_sizes());
        assert_eq!(expect, drinkup.intake_sizes.as_slice());
    }

    #[test]
    fn add_intake_size() {
        let mut drinkup = new_fake().unwrap();

        let add = IntakeSize {
            id: 2,
            name: "Test2".to_owned(),
            amount: 10,
        };
        let expect = &[
            IntakeSize {
                id: 1,
                name: "Test".to_owned(),
                amount: 5,
            },
            add.clone(),
        ];

        drinkup.save_intake_size(&add).unwrap();

        assert_eq!(expect, drinkup.intake_sizes());
        assert_eq!(expect, drinkup.intake_sizes.as_slice());
    }

    #[test]
    fn edit_intake_size() {
        let mut drinkup = new_fake().unwrap();

        let edit = IntakeSize {
            id: 1,
            name: "Test2".to_owned(),
            amount: 10,
        };
        let expect = &[edit.clone()];

        drinkup.save_intake_size(&edit).unwrap();

        assert_eq!(expect, drinkup.intake_sizes());
        assert_eq!(expect, drinkup.intake_sizes.as_slice());
    }

    #[test]
    fn get_history_today() {
        let mut drinkup = new_fake().unwrap();

        let expect = &[Record {
            id: 1,
            timestamp: Utc::now().date().and_hms(0, 0, 0),
            name: "Test".to_owned(),
            amount: 5,
        }];

        assert_eq!(expect, drinkup.history(Utc::now().date()).unwrap().as_ref());
        assert_eq!(expect, drinkup.today_history.as_slice());
    }

    #[test]
    fn get_history_yesterday() {
        let mut drinkup = new_fake().unwrap();

        let expect: &[Record] = &[];
        let tomorrow = Utc::now().date().add(Duration::days(1));

        assert_eq!(expect, drinkup.history(tomorrow).unwrap().as_ref());
    }

    #[test]
    fn add_record() {
        let mut drinkup = new_fake().unwrap();

        let record = Record {
            id: 2,
            timestamp: Utc::now().date().and_hms(0, 0, 0),
            name: "Test".to_owned(),
            amount: 10,
        };
        let expect = &[
            Record {
                id: 1,
                timestamp: Utc::now().date().and_hms(0, 0, 0),
                name: "Test".to_owned(),
                amount: 5,
            },
            record.clone(),
        ];

        drinkup.add_record(&record).unwrap();

        assert_eq!(expect, drinkup.history(Utc::now().date()).unwrap().as_ref());
        assert_eq!(expect, drinkup.today_history.as_slice());
    }

    #[test]
    fn get_progress() {
        let mut drinkup = new_fake().unwrap();
        assert_eq!(5, drinkup.progress().unwrap());
    }

    #[test]
    fn get_next_alarm() {
        new_fake().unwrap().next_alarm().unwrap();
    }

    #[test]
    fn get_drink_amount() {
        new_fake().unwrap().drink_amount().unwrap();
    }
}
