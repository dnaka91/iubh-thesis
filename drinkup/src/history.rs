use std::{
    fs::{self, File, OpenOptions},
    io::{prelude::*, BufReader, BufWriter},
    path::{Path, PathBuf},
};

use chrono::prelude::*;

use crate::{errors::Result, models::Record};

pub trait HistoryRepository {
    fn load(&self, date: Date<Utc>) -> Result<Vec<Record>>;
    fn save(&self, record: &Record) -> Result<()>;
}

pub struct JsonHistoryRepository {
    file_path: PathBuf,
}

#[must_use]
pub fn new_repo(base_path: &Path) -> JsonHistoryRepository {
    JsonHistoryRepository {
        file_path: base_path.join("history"),
    }
}

impl HistoryRepository for JsonHistoryRepository {
    fn load(&self, date: Date<Utc>) -> Result<Vec<Record>> {
        let file_path = self.file_path.join(date.format("%Y%m%d.json").to_string());
        if !file_path.exists() {
            return Ok(Vec::new());
        }

        let file = File::open(file_path)?;
        let file = BufReader::new(file);

        file.lines()
            .map(|l| serde_json::from_str(&l?).map_err(Into::into))
            .collect()
    }

    fn save(&self, record: &Record) -> Result<()> {
        fs::create_dir_all(&self.file_path)?;

        let file_path = self
            .file_path
            .join(Utc::now().date().format("%Y%m%d.json").to_string());

        let file = OpenOptions::new()
            .append(true)
            .create(true)
            .open(file_path)?;
        let mut file = BufWriter::new(file);

        serde_json::to_writer(&mut file, record)?;
        file.write_all(&[b'\n'])?;

        file.flush().map_err(Into::into)
    }
}

#[cfg(test)]
pub(crate) mod tests {
    use std::sync::Mutex;

    use super::*;

    pub struct FakeHistoryRepository {
        records: Mutex<Vec<Record>>,
    }

    impl FakeHistoryRepository {
        #[must_use]
        pub fn new(records: Vec<Record>) -> Self {
            Self {
                records: Mutex::new(records),
            }
        }
    }

    impl HistoryRepository for FakeHistoryRepository {
        fn load(&self, date: Date<Utc>) -> Result<Vec<Record>> {
            Ok(self
                .records
                .lock()
                .unwrap()
                .iter()
                .filter(|r| r.timestamp.date() == date)
                .cloned()
                .collect())
        }

        fn save(&self, record: &Record) -> Result<()> {
            self.records.lock().unwrap().push(record.clone());
            Ok(())
        }
    }

    #[test]
    fn load_empty() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let expect: &[Record] = &[];

        assert_eq!(expect, repo.load(Utc.ymd(2020, 1, 1)).unwrap());
    }

    #[test]
    fn save_and_load() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let record = Record {
            id: 1,
            timestamp: Utc.ymd(2020, 1, 1).and_hms(0, 0, 0),
            name: "Test".to_owned(),
            amount: 5,
        };
        let expect: &[Record] = &[record.clone()];

        repo.save(&record).unwrap();

        assert_eq!(expect, repo.load(Utc::now().date()).unwrap());
    }
}
