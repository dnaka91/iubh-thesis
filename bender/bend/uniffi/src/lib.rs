#![deny(rust_2018_idioms, clippy::all)]

use crate::{callback::*, empty::*, hello::*, mandelbrot::*, person::*};

mod callback;
mod empty;
mod hello;
mod mandelbrot;
mod person;

include!(concat!(env!("OUT_DIR"), "/bend.uniffi.rs"));

fn init() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Info)
            .with_tag("BEND_UNIFFI"),
    );
}
