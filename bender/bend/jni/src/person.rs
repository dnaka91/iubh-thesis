use bend::person::{self, Address, Gender, NaiveDate, Name, Person};
use chrono::prelude::*;
use jni::{
    objects::{JClass, JObject, JString},
    sys::{jclass, jint, jlong, jstring},
    JNIEnv,
};

pub type Result<T> = std::result::Result<T, Error>;

#[derive(Debug, thiserror::Error)]
pub enum Error {
    #[error("failure in bend")]
    Bend(#[from] bend::person::Error),
    #[error("failure in JNI")]
    Jni(#[from] jni::errors::Error),
    #[error("failue in JSON")]
    Json(#[from] serde_json::Error),
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_loadPerson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    id: jlong,
) -> jclass {
    let res = || {
        let person = person::load(id as u64)?;
        create_person(env, person)
    };

    match res() {
        Ok(obj) => obj.into_inner(),
        Err(e) => {
            env.throw(e.to_string()).unwrap();
            std::ptr::null_mut()
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_loadPersonJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    id: jlong,
) -> jstring {
    let res = || -> Result<_> {
        let person = person::load(id as u64)?;
        let json = serde_json::to_string(&person)?;
        env.new_string(json).map_err(Into::into)
    };

    match res() {
        Ok(value) => value.into_inner(),
        Err(e) => {
            env.throw(e.to_string()).unwrap();
            std::ptr::null_mut()
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_savePerson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    person: JObject<'_>,
) {
    let person = match extract_person(env, person) {
        Ok(p) => p,
        Err(e) => {
            env.throw(e.to_string()).unwrap();
            return;
        }
    };

    if let Err(e) = person::save(person) {
        env.throw(e.to_string()).unwrap();
    }
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_savePersonJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    json: JString<'_>,
) {
    let json: String = env.get_string(json).unwrap().into();
    let person = serde_json::from_str(&json).unwrap();

    if let Err(e) = person::save(person) {
        env.throw(e.to_string()).unwrap();
    }
}

/// Convert a [`Person`] into its Java equivalent.
pub fn create_person(env: JNIEnv<'_>, person: Person) -> Result<JObject<'_>> {
    let class = env.find_class("com/github/dnaka91/bender/jni/Person")?;

    Ok(env.new_object(
        class,
        "(JLcom/github/dnaka91/bender/jni/Name;Lcom/github/dnaka91/bender/jni/Gender;Ljava/time/LocalDate;Ljava/util/List;DJ)V",
        &[
            (person.id as jlong).into(),
            create_name(env, person.name)?.into(),
            create_gender(env, person.gender)?.into(),
            create_localdate(env, person.birthday)?.into(),
            create_arraylist(
                env,
                person.addresses.into_iter().map(|a| create_address(env, a)),
            )?
            .into(),
            person.weight.into(),
            (person.total_steps as jlong).into(),
        ],
    )?)
}

pub fn extract_person(env: JNIEnv<'_>, person: JObject<'_>) -> Result<Person> {
    let id = env.call_method(person, "getId", "()J", &[])?.j()? as u64;

    let name = env
        .call_method(
            person,
            "getName",
            "()Lcom/github/dnaka91/bender/jni/Name;",
            &[],
        )?
        .l()?;
    let name = extract_name(env, name)?;

    let gender = env
        .call_method(
            person,
            "getGender",
            "()Lcom/github/dnaka91/bender/jni/Gender;",
            &[],
        )?
        .l()?;
    let gender = extract_gender(env, gender)?;

    let birthday = env
        .call_method(person, "getBirthday", "()Ljava/time/LocalDate;", &[])?
        .l()?;
    let birthday = extract_localdate(env, birthday)?;

    let addresses = env
        .call_method(person, "getAddresses", "()Ljava/util/List;", &[])?
        .l()?;
    let addresses = extract_arraylist(env, addresses, |item| extract_address(env, item))?;

    let weight = env.call_method(person, "getWeight", "()D", &[])?.d()?;
    let total_steps = env.call_method(person, "getTotalSteps", "()J", &[])?.j()? as u64;

    Ok(Person {
        id,
        name,
        gender,
        birthday,
        addresses,
        weight,
        total_steps,
    })
}

/// Convert a [`Name`] into its Java equivalent.
fn create_name(env: JNIEnv<'_>, name: Name) -> Result<JObject<'_>> {
    let class = env.find_class("com/github/dnaka91/bender/jni/Name")?;

    Ok(env.new_object(
        class,
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        &[
            name.title
                .map_or_else(
                    || Ok(JObject::null()),
                    |s| env.new_string(s).map(Into::into),
                )?
                .into(),
            env.new_string(name.first)?.into(),
            name.middle
                .map_or_else(
                    || Ok(JObject::null()),
                    |s| env.new_string(s).map(Into::into),
                )?
                .into(),
            env.new_string(name.last)?.into(),
        ],
    )?)
}

fn extract_name(env: JNIEnv<'_>, name: JObject<'_>) -> Result<Name> {
    let title = env
        .call_method(name, "getTitle", "()Ljava/lang/String;", &[])?
        .l()?;
    let title = if title.is_null() {
        None
    } else {
        Some(env.get_string(title.into())?.into())
    };

    let first = env
        .call_method(name, "getFirst", "()Ljava/lang/String;", &[])?
        .l()?;
    let first = env.get_string(first.into())?.into();

    let middle = env
        .call_method(name, "getMiddle", "()Ljava/lang/String;", &[])?
        .l()?;
    let middle = if middle.is_null() {
        None
    } else {
        Some(env.get_string(middle.into())?.into())
    };

    let last = env
        .call_method(name, "getLast", "()Ljava/lang/String;", &[])?
        .l()?;
    let last = env.get_string(last.into())?.into();

    Ok(Name {
        title,
        first,
        middle,
        last,
    })
}

/// Convert a [`Gender`] into its Java equivalent.
fn create_gender(env: JNIEnv<'_>, gender: Gender) -> Result<JObject<'_>> {
    Ok(match gender {
        Gender::Female => {
            let class = env.find_class("com/github/dnaka91/bender/jni/Gender$Female")?;

            env.get_static_field(
                class,
                "INSTANCE",
                "Lcom/github/dnaka91/bender/jni/Gender$Female;",
            )?
            .l()?
        }
        Gender::Male => {
            let class = env.find_class("com/github/dnaka91/bender/jni/Gender$Male")?;

            env.get_static_field(
                class,
                "INSTANCE",
                "Lcom/github/dnaka91/bender/jni/Gender$Male;",
            )?
            .l()?
        }
        Gender::Other(value) => {
            let class = env.find_class("com/github/dnaka91/bender/jni/Gender$Other")?;

            env.new_object(
                class,
                "(Ljava/lang/String;)V",
                &[env.new_string(value)?.into()],
            )?
        }
    })
}

fn extract_gender(env: JNIEnv<'_>, gender: JObject<'_>) -> Result<Gender> {
    let class_female = env.find_class("com/github/dnaka91/bender/jni/Gender$Female")?;
    let class_male = env.find_class("com/github/dnaka91/bender/jni/Gender$Male")?;
    let class_other = env.find_class("com/github/dnaka91/bender/jni/Gender$Other")?;

    if env.is_instance_of(gender, class_female)? {
        Ok(Gender::Female)
    } else if env.is_instance_of(gender, class_male)? {
        Ok(Gender::Male)
    } else if env.is_instance_of(gender, class_other)? {
        let value = env
            .call_method(gender, "getValue", "()Ljava/lang/String;", &[])?
            .l()?;
        let value = env.get_string(value.into())?.into();
        Ok(Gender::Other(value))
    } else {
        unreachable!()
    }
}

/// Convert an [`Address`] into its Java equivalent.
fn create_address(env: JNIEnv<'_>, address: Address) -> Result<JObject<'_>> {
    let class = env.find_class("com/github/dnaka91/bender/jni/Address")?;

    Ok(env.new_object(class, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", &[
        env.new_string(address.street)?.into(),
        env.new_string(address.house_number)?.into(),
        env.new_string(address.city)?.into(),
        env.new_string(address.country)?.into(),
        env.new_string(address.postal_code)?.into(),
        create_arraylist(env, address.details.into_iter().map(|d|env.new_string(d).map(Into::into).map_err(Into::into)))?.into(),
    ])?)
}

fn extract_address(env: JNIEnv<'_>, address: JObject<'_>) -> Result<Address> {
    let street = env
        .call_method(address, "getStreet", "()Ljava/lang/String;", &[])?
        .l()?;
    let street = env.get_string(street.into())?.into();

    let house_number = env
        .call_method(address, "getHouseNumber", "()Ljava/lang/String;", &[])?
        .l()?;
    let house_number = env.get_string(house_number.into())?.into();

    let city = env
        .call_method(address, "getCity", "()Ljava/lang/String;", &[])?
        .l()?;
    let city = env.get_string(city.into())?.into();

    let country = env
        .call_method(address, "getCountry", "()Ljava/lang/String;", &[])?
        .l()?;
    let country = env.get_string(country.into())?.into();

    let postal_code = env
        .call_method(address, "getPostalCode", "()Ljava/lang/String;", &[])?
        .l()?;
    let postal_code = env.get_string(postal_code.into())?.into();

    let details = env
        .call_method(address, "getDetails", "()Ljava/util/List;", &[])?
        .l()?;
    let details = extract_arraylist(env, details, |item| Ok(env.get_string(item.into())?.into()))?;

    Ok(Address {
        street,
        house_number,
        city,
        country,
        postal_code,
        details,
    })
}

/// Convert the iterator into a `java.util.ArrayList`, filling it with the iterator items.
fn create_arraylist<'a>(
    env: JNIEnv<'a>,
    items: impl Iterator<Item = Result<JObject<'a>>>,
) -> Result<JObject<'a>> {
    let class = env.find_class("java/util/ArrayList")?;
    let list = env.new_object(class, "()V", &[])?;

    for item in items {
        env.call_method(list, "add", "(Ljava/lang/Object;)Z", &[item?.into()])?;
    }

    Ok(list)
}

fn extract_arraylist<T>(
    env: JNIEnv<'_>,
    list: JObject<'_>,
    mapper: impl Fn(JObject<'_>) -> Result<T>,
) -> Result<Vec<T>> {
    let size = env.call_method(list, "size", "()I", &[])?.i()? as usize;
    let mut buf = Vec::with_capacity(size);

    for i in 0..size {
        let item = env
            .call_method(list, "get", "(I)Ljava/lang/Object;", &[(i as jint).into()])?
            .l()?;
        buf.push(mapper(item)?);
    }

    Ok(buf)
}

/// Convert a date with UTC timezone into a `java.time.LocalDate`.
fn create_localdate(env: JNIEnv<'_>, date: NaiveDate) -> Result<JObject<'_>> {
    let class = env.find_class("java/time/LocalDate")?;

    Ok(env
        .call_static_method(
            class,
            "of",
            "(III)Ljava/time/LocalDate;",
            &[
                date.year().into(),
                (date.month() as jint).into(),
                (date.day() as jint).into(),
            ],
        )?
        .l()?)
}

fn extract_localdate(env: JNIEnv<'_>, date: JObject<'_>) -> Result<NaiveDate> {
    let year = env.call_method(date, "getYear", "()I", &[])?.i()?;
    let month = env.call_method(date, "getMonthValue", "()I", &[])?.i()? as u32;
    let day = env.call_method(date, "getDayOfMonth", "()I", &[])?.i()? as u32;

    Ok(NaiveDate::from_ymd(year, month, day))
}
