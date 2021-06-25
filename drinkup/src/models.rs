use chrono::prelude::*;
use serde::{Deserialize, Serialize};

pub type Id = i32;

#[derive(Clone, Debug, Serialize, Deserialize)]
#[cfg_attr(test, derive(PartialEq, Eq))]
pub struct Schedule {
    pub start: NaiveTime,
    pub end: NaiveTime,
    pub goal: u32,
}

#[derive(Clone, Debug, Serialize, Deserialize)]
#[cfg_attr(test, derive(PartialEq, Eq))]
pub struct Record {
    pub id: Id,
    pub timestamp: DateTime<Utc>,
    pub name: String,
    pub amount: u32,
}

#[derive(Clone, Debug, Serialize, Deserialize)]
#[cfg_attr(test, derive(PartialEq, Eq))]
pub struct IntakeSize {
    pub id: Id,
    pub name: String,
    pub amount: u32,
}
