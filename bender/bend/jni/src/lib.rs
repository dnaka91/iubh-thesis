#![deny(rust_2018_idioms, clippy::all)]
#![allow(clippy::missing_safety_doc)]

use jni::{objects::JClass, JNIEnv};

mod callback;
mod empty;
mod hello;
mod mandelbrot;
mod person;

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_init(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Info)
            .with_tag("BEND_JNI"),
    );
}
