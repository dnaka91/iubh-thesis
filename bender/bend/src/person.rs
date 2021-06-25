pub use chrono::NaiveDate;
use log::info;
use serde::{Deserialize, Serialize};

pub type Result<T> = std::result::Result<T, Error>;

#[derive(Debug, thiserror::Error)]
pub enum Error {
    #[error("Something bad happened")]
    Invalid,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Person {
    pub id: u64,
    pub name: Name,
    pub gender: Gender,
    pub birthday: NaiveDate,
    pub addresses: Vec<Address>,
    pub weight: f64,
    pub total_steps: u64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Name {
    pub title: Option<String>,
    pub first: String,
    pub middle: Option<String>,
    pub last: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub enum Gender {
    Female,
    Male,
    Other(String),
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Address {
    pub street: String,
    pub house_number: String,
    pub city: String,
    pub country: String,
    pub postal_code: String,
    pub details: Vec<String>,
}

pub fn load(id: u64) -> Result<Person> {
    Ok(Person {
        id,
        name: Name {
            title: Some("Doctor".to_owned()),
            first: "Max".to_owned(),
            middle: None,
            last: "Mustermann".to_owned(),
        },
        gender: Gender::Male,
        birthday: NaiveDate::from_ymd(1990, 3, 14),
        addresses: vec![Address {
            street: "Sample Street 1-2".to_owned(),
            house_number: "33b".to_owned(),
            city: "Sampleton".to_owned(),
            country: "Samplevania".to_owned(),
            postal_code: "111-222".to_owned(),
            details: vec!["Room 5".to_owned()],
        }],
        weight: 72.5,
        total_steps: 33095,
    })
}

pub fn save(person: Person) -> Result<()> {
    info!("{:#?}", person);
    Ok(())
}
