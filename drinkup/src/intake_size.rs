use std::{
    fs,
    path::{Path, PathBuf},
};

use chrono::prelude::*;
use serde::{Deserialize, Serialize};

use crate::{
    errors::Result,
    models::{Id, IntakeSize},
};

#[derive(Serialize, Deserialize)]
struct IntakeSizeWrapper {
    sizes: Vec<IntakeSize>,
}

pub trait IntakeSizeRepository {
    fn list(&self) -> Result<Vec<IntakeSize>>;
    fn save(&self, intake_size: &IntakeSize) -> Result<Id>;
}

pub struct TomlIntakeSizeRepository {
    file_path: PathBuf,
}

#[must_use]
pub fn new_repo(base_path: &Path) -> TomlIntakeSizeRepository {
    TomlIntakeSizeRepository {
        file_path: base_path.join("intake_sizes.toml"),
    }
}

impl IntakeSizeRepository for TomlIntakeSizeRepository {
    fn list(&self) -> Result<Vec<IntakeSize>> {
        if !self.file_path.exists() {
            return Ok(vec![
                IntakeSize {
                    id: 1,
                    name: "Small Cup".to_owned(),
                    amount: 150,
                },
                IntakeSize {
                    id: 2,
                    name: "Big Cup".to_owned(),
                    amount: 250,
                },
                IntakeSize {
                    id: 3,
                    name: "Bottle".to_owned(),
                    amount: 500,
                },
            ]);
        }

        let data = fs::read(&self.file_path)?;
        toml::from_slice::<IntakeSizeWrapper>(&data)
            .map(|w| w.sizes)
            .map_err(Into::into)
    }

    #[allow(clippy::option_if_let_else)]
    fn save(&self, intake_size: &IntakeSize) -> Result<Id> {
        let mut intake_sizes = self.list().unwrap_or_default();
        let id = if let Some(is) = intake_sizes.iter_mut().find(|is| is.id == intake_size.id) {
            is.name = intake_size.name.clone();
            is.amount = intake_size.amount;

            is.id
        } else {
            let mut clone = intake_size.clone();
            if clone.id == 0 {
                clone.id = intake_sizes
                    .iter()
                    .max_by(|a, b| a.id.cmp(&b.id))
                    .map(|is| is.id + 1)
                    .unwrap_or_default();
            }

            let id = clone.id;
            intake_sizes.push(clone);

            id
        };

        let data = toml::to_vec(&IntakeSizeWrapper {
            sizes: intake_sizes,
        })?;
        fs::write(&self.file_path, data)?;

        Ok(id)
    }
}

#[cfg(test)]
pub(crate) mod tests {
    use std::sync::Mutex;

    use super::*;

    pub struct FakeIntakeSizeRepository {
        sizes: Mutex<Vec<IntakeSize>>,
    }

    impl FakeIntakeSizeRepository {
        #[must_use]
        pub fn new(sizes: Vec<IntakeSize>) -> Self {
            Self {
                sizes: Mutex::new(sizes),
            }
        }
    }

    impl IntakeSizeRepository for FakeIntakeSizeRepository {
        fn list(&self) -> Result<Vec<IntakeSize>> {
            Ok(self.sizes.lock().unwrap().clone())
        }

        fn save(&self, intake_size: &IntakeSize) -> Result<Id> {
            self.sizes.lock().unwrap().push(intake_size.clone());
            Ok(intake_size.id)
        }
    }

    #[test]
    fn load_empty() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let expect = &[
            IntakeSize {
                id: 1,
                name: "Small Cup".to_owned(),
                amount: 150,
            },
            IntakeSize {
                id: 2,
                name: "Big Cup".to_owned(),
                amount: 250,
            },
            IntakeSize {
                id: 3,
                name: "Bottle".to_owned(),
                amount: 500,
            },
        ];

        assert_eq!(expect, repo.list().unwrap().as_slice());
    }

    #[test]
    fn add() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let add = IntakeSize {
            id: 4,
            name: "Test".to_owned(),
            amount: 5,
        };
        let expect = &[
            IntakeSize {
                id: 1,
                name: "Small Cup".to_owned(),
                amount: 150,
            },
            IntakeSize {
                id: 2,
                name: "Big Cup".to_owned(),
                amount: 250,
            },
            IntakeSize {
                id: 3,
                name: "Bottle".to_owned(),
                amount: 500,
            },
            add.clone(),
        ];

        repo.save(&add).unwrap();

        assert!(dir.path().join("intake_sizes.toml").exists());
        assert_eq!(expect, repo.list().unwrap().as_slice());
    }

    #[test]
    fn edit() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let edit = IntakeSize {
            id: 2,
            name: "Test".to_owned(),
            amount: 5,
        };
        let expect = &[
            IntakeSize {
                id: 1,
                name: "Small Cup".to_owned(),
                amount: 150,
            },
            edit.clone(),
            IntakeSize {
                id: 3,
                name: "Bottle".to_owned(),
                amount: 500,
            },
        ];

        repo.save(&edit).unwrap();

        assert!(dir.path().join("intake_sizes.toml").exists());
        assert_eq!(expect, repo.list().unwrap().as_slice());
    }

    #[test]
    fn add_new_id() {
        let dir = tempfile::tempdir().unwrap();
        let repo = new_repo(dir.path());

        let add = IntakeSize {
            id: 0,
            name: "Test".to_owned(),
            amount: 5,
        };
        let expect = &[
            IntakeSize {
                id: 1,
                name: "Small Cup".to_owned(),
                amount: 150,
            },
            IntakeSize {
                id: 2,
                name: "Big Cup".to_owned(),
                amount: 250,
            },
            IntakeSize {
                id: 3,
                name: "Bottle".to_owned(),
                amount: 500,
            },
            IntakeSize {
                id: 4,
                name: "Test".to_owned(),
                amount: 5,
            },
        ];

        repo.save(&add).unwrap();

        assert!(dir.path().join("intake_sizes.toml").exists());
        assert_eq!(expect, repo.list().unwrap().as_slice());
    }
}
