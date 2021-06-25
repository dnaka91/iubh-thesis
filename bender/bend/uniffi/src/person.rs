use bend::person::{self, NaiveDate};
use chrono::Datelike;

#[derive(Debug, thiserror::Error)]
pub enum Error {
    #[error("Something bad happened")]
    Invalid,
}

impl From<person::Error> for Error {
    fn from(e: person::Error) -> Self {
        match e {
            person::Error::Invalid => Self::Invalid,
        }
    }
}

pub fn load_person(id: u64) -> Result<Person, Error> {
    person::load(id).map(Into::into).map_err(Into::into)
}

pub fn save_person(person: Person) -> Result<(), Error> {
    person::save(person.into()).map_err(Into::into)
}

pub struct Person {
    pub id: u64,
    pub name: Name,
    pub gender: Gender,
    pub birthday: SimpleDate,
    pub addresses: Vec<Address>,
    pub weight: f64,
    pub total_steps: u64,
}

impl From<person::Person> for Person {
    fn from(p: person::Person) -> Self {
        Self {
            id: p.id,
            name: p.name.into(),
            gender: p.gender.into(),
            birthday: p.birthday.into(),
            addresses: p.addresses.into_iter().map(Into::into).collect(),
            weight: p.weight,
            total_steps: p.total_steps,
        }
    }
}

impl From<Person> for person::Person {
    fn from(p: Person) -> Self {
        Self {
            id: p.id,
            name: p.name.into(),
            gender: p.gender.into(),
            birthday: p.birthday.into(),
            addresses: p.addresses.into_iter().map(Into::into).collect(),
            weight: p.weight,
            total_steps: p.total_steps,
        }
    }
}

pub struct Name {
    pub title: Option<String>,
    pub first: String,
    pub middle: Option<String>,
    pub last: String,
}

impl From<person::Name> for Name {
    fn from(n: person::Name) -> Self {
        Self {
            title: n.title,
            first: n.first,
            middle: n.middle,
            last: n.last,
        }
    }
}

impl From<Name> for person::Name {
    fn from(n: Name) -> Self {
        Self {
            title: n.title,
            first: n.first,
            middle: n.middle,
            last: n.last,
        }
    }
}

pub enum Gender {
    Female,
    Male,
    Other,
}

impl From<person::Gender> for Gender {
    fn from(g: person::Gender) -> Self {
        match g {
            person::Gender::Female => Self::Female,
            person::Gender::Male => Self::Male,
            person::Gender::Other(_) => Self::Other,
        }
    }
}

impl From<Gender> for person::Gender {
    fn from(g: Gender) -> Self {
        match g {
            Gender::Female => Self::Female,
            Gender::Male => Self::Male,
            Gender::Other => Self::Other(String::new()),
        }
    }
}

pub struct Address {
    pub street: String,
    pub house_number: String,
    pub city: String,
    pub country: String,
    pub postal_code: String,
    pub details: Vec<String>,
}

impl From<person::Address> for Address {
    fn from(a: person::Address) -> Self {
        Self {
            street: a.street,
            house_number: a.house_number,
            city: a.city,
            country: a.country,
            postal_code: a.postal_code,
            details: a.details,
        }
    }
}

impl From<Address> for person::Address {
    fn from(a: Address) -> Self {
        Self {
            street: a.street,
            house_number: a.house_number,
            city: a.city,
            country: a.country,
            postal_code: a.postal_code,
            details: a.details,
        }
    }
}

pub struct SimpleDate {
    pub year: i32,
    pub month: u32,
    pub day: u32,
}

impl From<NaiveDate> for SimpleDate {
    fn from(d: NaiveDate) -> Self {
        Self {
            year: d.year(),
            month: d.month(),
            day: d.day(),
        }
    }
}

impl From<SimpleDate> for NaiveDate {
    fn from(d: SimpleDate) -> Self {
        Self::from_ymd(d.year, d.month, d.day)
    }
}
