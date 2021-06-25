#![deny(rust_2018_idioms, clippy::all)]
#![allow(clippy::missing_safety_doc)]

mod callback;
mod empty;
mod hello;
mod mandelbrot;
mod person;

#[no_mangle]
pub extern "C" fn bend_init() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Info)
            .with_tag("BEND_RAW"),
    );
}
