use std::{
    fs,
    path::{Path, PathBuf},
};

use chrono::prelude::*;

use crate::{errors::Result, models::Schedule};

pub trait ScheduleRepository {
    fn load(&self) -> Result<Schedule>;
    fn save(&self, schedule: &Schedule) -> Result<()>;
}

pub struct TomlScheduleRepository {
    file_path: PathBuf,
}

#[must_use]
pub fn new_repo(base_path: &Path) -> TomlScheduleRepository {
    TomlScheduleRepository {
        file_path: base_path.join("schedule.toml"),
    }
}

impl ScheduleRepository for TomlScheduleRepository {
    fn load(&self) -> Result<Schedule> {
        if !self.file_path.exists() {
            return Ok(Schedule {
                start: NaiveTime::from_hms(8, 0, 0),
                end: NaiveTime::from_hms(22, 0, 0),
                goal: 2400,
            });
        }

        let data = fs::read(&self.file_path)?;
        toml::from_slice(&data).map_err(Into::into)
    }

    fn save(&self, schedule: &Schedule) -> Result<()> {
        let data = toml::to_vec(schedule)?;
        fs::write(&self.file_path, data).map_err(Into::into)
    }
}

#[cfg(test)]
pub(crate) mod tests {
    use std::sync::Mutex;

    use super::*;

    pub struct FakeScheduleRepository {
        schedule: Mutex<Schedule>,
    }

    impl FakeScheduleRepository {
        #[must_use]
        pub fn new(schedule: Schedule) -> Self {
            Self {
                schedule: Mutex::new(schedule),
            }
        }
    }

    impl ScheduleRepository for FakeScheduleRepository {
        fn load(&self) -> Result<Schedule> {
            Ok(self.schedule.lock().unwrap().clone())
        }

        fn save(&self, schedule: &Schedule) -> Result<()> {
            *self.schedule.lock().unwrap() = schedule.clone();
            Ok(())
        }
    }

    #[test]
    fn load_empty() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let expect = Schedule {
            start: NaiveTime::from_hms(8, 0, 0),
            end: NaiveTime::from_hms(22, 0, 0),
            goal: 2400,
        };

        assert_eq!(expect, repo.load().unwrap());
    }

    #[test]
    fn save_and_load() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let schedule = Schedule {
            start: NaiveTime::from_hms(12, 10, 0),
            end: NaiveTime::from_hms(19, 0, 15),
            goal: 1000,
        };

        repo.save(&schedule).unwrap();

        assert!(dir.path().join("schedule.toml").exists());
        assert_eq!(schedule, repo.load().unwrap());
    }
}
