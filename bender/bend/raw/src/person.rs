use std::{
    ffi::CString,
    mem,
    os::raw::{c_char, c_double, c_int, c_uint, c_ulonglong},
    ptr,
};

use bend::person::{self, Address, Gender, Name, Person};
use chrono::prelude::*;

#[no_mangle]
pub unsafe extern "C" fn load_person(id: u64) -> *const Person {
    let person = person::load(id).unwrap();
    Box::into_raw(Box::new(person))
}

#[no_mangle]
pub unsafe extern "C" fn load_person_json(id: u64) -> *const c_char {
    let person = person::load(id).unwrap();
    let json = serde_json::to_string(&person).unwrap();

    json.into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn load_person_direct(id: u64) -> *const CPerson {
    let person = person::load(id).unwrap();
    Box::into_raw(Box::new(CPerson {
        id: person.id,
        name: CName {
            title: person.name.title.into_ptr(),
            first: person.name.first.into_ptr(),
            middle: person.name.middle.into_ptr(),
            last: person.name.last.into_ptr(),
        },
        gender: match person.gender {
            Gender::Female => CGender::Female,
            Gender::Male => CGender::Male,
            Gender::Other(_) => CGender::Other,
        },
        birthday: CDate {
            year: person.birthday.year(),
            month: person.birthday.month(),
            day: person.birthday.day(),
        },
        addresses_len: person.addresses.len() as c_uint,
        addresses: person
            .addresses
            .into_iter()
            .map(|a| {
                Box::into_raw(Box::new(CAddress {
                    street: a.street.into_ptr(),
                    house_number: a.house_number.into_ptr(),
                    city: a.city.into_ptr(),
                    country: a.country.into_ptr(),
                    postal_code: a.postal_code.into_ptr(),
                    details_len: a.details.len() as c_uint,
                    details: a
                        .details
                        .into_iter()
                        .map(|d| d.into_ptr())
                        .collect::<Vec<_>>()
                        .into_ptr(),
                })) as *const _
            })
            .collect::<Vec<_>>()
            .into_ptr(),
        weight: person.weight,
        total_steps: person.total_steps,
    }))
}

#[no_mangle]
pub unsafe extern "C" fn person_free(person: u64) {
    Box::from_raw(person as *mut Person);
}

#[no_mangle]
pub unsafe extern "C" fn person_get_id(person: u64) -> u64 {
    let person = &*(person as *const Person);
    person.id
}

#[no_mangle]
pub unsafe extern "C" fn person_get_name(person: u64) -> *const Name {
    let person = &*(person as *const Person);
    mem::transmute(&person.name)
}

#[no_mangle]
pub unsafe extern "C" fn person_get_gender(person: u64) -> u32 {
    let person = &*(person as *const Person);
    match person.gender {
        Gender::Female => 0,
        Gender::Male => 1,
        Gender::Other(_) => 2,
    }
}

#[no_mangle]
pub unsafe extern "C" fn person_get_birthday_year(person: u64) -> u32 {
    let person = &*(person as *const Person);
    person.birthday.year() as u32
}

#[no_mangle]
pub unsafe extern "C" fn person_get_birthday_month(person: u64) -> u32 {
    let person = &*(person as *const Person);
    person.birthday.month() as u32
}

#[no_mangle]
pub unsafe extern "C" fn person_get_birthday_day(person: u64) -> u32 {
    let person = &*(person as *const Person);
    person.birthday.day() as u32
}

#[no_mangle]
pub unsafe extern "C" fn person_get_address(person: u64, index: u64) -> *const Address {
    let person = &*(person as *const Person);
    mem::transmute(&person.addresses[index as usize])
}

#[no_mangle]
pub unsafe extern "C" fn person_get_address_len(person: u64) -> u64 {
    let person = &*(person as *const Person);
    person.addresses.len() as u64
}

#[no_mangle]
pub unsafe extern "C" fn person_get_weight(person: u64) -> f64 {
    let person = &*(person as *const Person);
    person.weight
}

#[no_mangle]
pub unsafe extern "C" fn person_get_total_steps(person: u64) -> u64 {
    let person = &*(person as *const Person);
    person.total_steps
}

#[no_mangle]
pub unsafe extern "C" fn name_get_title(name: u64) -> *const c_char {
    let name = &*(name as *const Name);
    name.title.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn name_get_first(name: u64) -> *const c_char {
    let name = &*(name as *const Name);
    name.first.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn name_get_middle(name: u64) -> *const c_char {
    let name = &*(name as *const Name);
    name.middle.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn name_get_last(name: u64) -> *const c_char {
    let name = &*(name as *const Name);
    name.last.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_street(address: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.street.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_house_number(address: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.house_number.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_city(address: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.city.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_country(address: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.country.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_postal_code(address: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.postal_code.clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_detail(address: u64, index: u64) -> *const c_char {
    let address = &*(address as *const Address);
    address.details[index as usize].clone().into_ptr()
}

#[no_mangle]
pub unsafe extern "C" fn address_get_detail_len(address: u64) -> u64 {
    let address = &*(address as *const Address);
    address.details.len() as u64
}

#[repr(C)]
pub struct CPerson {
    pub id: c_ulonglong,
    pub name: CName,
    pub gender: CGender,
    pub birthday: CDate,
    pub addresses: *const *const CAddress,
    pub addresses_len: c_uint,
    pub weight: c_double,
    pub total_steps: c_ulonglong,
}

#[repr(C)]
pub struct CName {
    pub title: *const c_char,
    pub first: *const c_char,
    pub middle: *const c_char,
    pub last: *const c_char,
}

#[repr(C)]
pub enum CGender {
    Female = 0,
    Male = 1,
    Other = 2,
}

#[repr(C)]
pub struct CDate {
    pub year: c_int,
    pub month: c_uint,
    pub day: c_uint,
}

#[repr(C)]
pub struct CAddress {
    pub street: *const c_char,
    pub house_number: *const c_char,
    pub city: *const c_char,
    pub country: *const c_char,
    pub postal_code: *const c_char,
    pub details: *const *const c_char,
    pub details_len: c_uint,
}

unsafe trait StringExt {
    fn into_ptr(self) -> *const c_char;
}

unsafe impl StringExt for String {
    fn into_ptr(self) -> *const c_char {
        let cstr = CString::new(self).unwrap();

        let p = cstr.as_ptr();
        mem::forget(cstr);

        p
    }
}

unsafe impl StringExt for &str {
    fn into_ptr(self) -> *const c_char {
        let cstr = CString::new(self).unwrap();

        let p = cstr.as_ptr();
        mem::forget(cstr);

        p
    }
}

unsafe impl StringExt for Option<String> {
    fn into_ptr(self) -> *const c_char {
        match self {
            Some(value) => {
                let title = CString::new(value).unwrap();

                let p = title.as_ptr();
                mem::forget(title);

                p
            }
            None => ptr::null(),
        }
    }
}

unsafe trait VecExt<T> {
    fn into_ptr(self) -> *const T;
}

unsafe impl<T> VecExt<T> for Vec<T> {
    fn into_ptr(self) -> *const T {
        let p = self.as_ptr();
        mem::forget(self);
        p
    }
}
