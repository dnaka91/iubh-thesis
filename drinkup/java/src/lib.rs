#![deny(rust_2018_idioms, clippy::all, clippy::pedantic)]
#![warn(clippy::nursery)]
#![allow(
    non_snake_case,
    clippy::cast_possible_wrap,
    clippy::cast_sign_loss,
    clippy::missing_const_for_fn
)]

use anyhow::Result;
use chrono::prelude::*;
use drinkup::DrinkUpDefault;
use jni::{
    objects::{JClass, JObject, JString},
    sys::{jint, jlong, jobject, jstring},
    JNIEnv,
};
use log::{debug, Level};

macro_rules! ensure_valid_ptr {
    ($env:ident, $ptr:ident) => {
        ensure_valid_ptr!($env, $ptr, Default::default())
    };
    ($env:ident, $ptr:ident, $value:expr) => {
        if $ptr == 0 {
            $env.throw_new("java/lang/NullPointerException", "").ok();
            return $value;
        }
    };
}

macro_rules! success_or_throw {
    ($env:ident, $result:expr) => {
        success_or_throw!($env, $result, Default::default())
    };
    ($env:ident, $result:expr, $default:expr) => {
        match $result {
            Ok(v) => v,
            Err(e) => {
                $env.throw(e.to_string()).ok();
                $default
            }
        }
    };
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_init(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(Level::Debug)
            .with_tag("DRINKUP"),
    );
    debug!("init()");
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_create(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    base_dir: JString<'_>,
) -> jlong {
    let run = || -> Result<_> {
        let base_dir = String::from(env.get_string(base_dir)?);
        debug!("create({})", base_dir);

        let d = drinkup::DrinkUp::new_default(base_dir)?;

        Ok(Box::into_raw(Box::new(d)) as jlong)
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_destroy(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        unsafe { Box::from_raw(ptr as *mut DrinkUpDefault) };
        debug!("destroy({:#x})", ptr);
        Ok(())
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_scheduleJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) -> jstring {
    ensure_valid_ptr!(env, ptr, std::ptr::null_mut());

    let run = || -> Result<_> {
        let d = unsafe { &*(ptr as *mut DrinkUpDefault) };

        let schedule = d.schedule();
        debug!("scheduleJson({:#x}) -> {:?}", ptr, schedule);

        let json = serde_json::to_string(&schedule)?;

        env.new_string(json)
            .map(|v| v.into_inner())
            .map_err(Into::into)
    };

    success_or_throw!(env, run(), std::ptr::null_mut())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_saveScheduleJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
    schedule: JString<'_>,
) {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };

        let schedule = String::from(env.get_string(schedule)?);
        let schedule = serde_json::from_str(&schedule)?;
        debug!("saveScheduleJson({:#x}, {:?})", ptr, schedule);

        d.save_schedule(&schedule).map_err(Into::into)
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_listIntakeSizesJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) -> jstring {
    ensure_valid_ptr!(env, ptr, std::ptr::null_mut());

    let run = || -> Result<_> {
        let d = unsafe { &*(ptr as *mut DrinkUpDefault) };

        let sizes = d.intake_sizes();
        debug!("listIntakeSizesJson({:#x}) -> {:?}", ptr, sizes);

        let json = serde_json::to_string(&sizes)?;

        env.new_string(json)
            .map(|v| v.into_inner())
            .map_err(Into::into)
    };

    success_or_throw!(env, run(), std::ptr::null_mut())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_saveIntakeSizeJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
    intake_size: JString<'_>,
) {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };

        let intake_size = String::from(env.get_string(intake_size)?);
        let intake_size = serde_json::from_str(&intake_size)?;
        debug!("saveIntakeSizeJson({:#x}, {:?})", ptr, intake_size);

        d.save_intake_size(&intake_size).map_err(Into::into)
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_historyJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
    timestamp: jlong,
) -> jstring {
    ensure_valid_ptr!(env, ptr, std::ptr::null_mut());

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };
        let date = Utc.timestamp(timestamp * 60 * 60 * 24, 0).date();

        let history = d.history(date)?;
        debug!("historyJson({:#x}, {}) -> {:?}", ptr, date, history);

        let json = serde_json::to_string(&history)?;

        env.new_string(json)
            .map(|v| v.into_inner())
            .map_err(Into::into)
    };

    success_or_throw!(env, run(), std::ptr::null_mut())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_addRecordJson(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
    record: JString<'_>,
) {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };

        let record = String::from(env.get_string(record)?);
        let record = serde_json::from_str(&record)?;
        debug!("addRecordJson({:#x}, {:?})", ptr, record);

        d.add_record(&record).map_err(Into::into)
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_progress(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) -> jint {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };

        let ret = d.progress()?;
        debug!("progress({:#x}) -> {}", ptr, ret);

        Ok(ret as jint)
    };

    success_or_throw!(env, run())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_nextAlarm(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) -> jobject {
    ensure_valid_ptr!(env, ptr, std::ptr::null_mut());

    let run = || -> Result<_> {
        let d = unsafe { &*(ptr as *mut DrinkUpDefault) };

        let ret = d.next_alarm()?;
        debug!("nextAlarm({:#x}) -> {:?}", ptr, ret);

        match ret {
            Some(v) => env
                .new_object(
                    env.find_class("java/lang/Long")?,
                    "(J)V",
                    &[(jlong::from(v)).into()],
                )
                .map(JObject::into_inner)
                .map_err(Into::into),
            None => Ok(std::ptr::null_mut()),
        }
    };

    success_or_throw!(env, run(), std::ptr::null_mut())
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_drinkup_rs_Drinkup_drinkAmount(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    ptr: jlong,
) -> jint {
    ensure_valid_ptr!(env, ptr);

    let run = || -> Result<_> {
        let d = unsafe { &mut *(ptr as *mut DrinkUpDefault) };

        let ret = d.drink_amount()?;
        debug!("drinkAmount({:#x}) -> {:?}", ptr, ret);

        Ok(ret as jint)
    };

    success_or_throw!(env, run())
}
