#![deny(rust_2018_idioms, clippy::all)]

use bend::{
    hello,
    mandelbrot::{self, Complex64},
    mandelbrot_manual, person,
};
use safer_ffi::prelude::*;

#[ffi_export]
fn init() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Info)
            .with_tag("BEND_SAFER_FFI"),
    );
}

#[ffi_export]
fn empty() {}

#[ffi_export]
fn hello(name: char_p::Ref<'_>) -> str::Box {
    let name = name.to_str();
    hello::greet(name).into()
}

#[ffi_export]
fn mandelbrot(width: u32, height: u32, result: *mut u8) {
    let res = mandelbrot::render(
        (width, height),
        Complex64 {
            re: -1.20,
            im: 0.35,
        },
        Complex64 {
            re: -1.00,
            im: 0.20,
        },
    );

    unsafe {
        result.copy_from(res.as_ptr(), res.len());
    }
}

#[ffi_export]
fn mandelbrot_manual(width: u32, height: u32, result: *mut u8) {
    let res = mandelbrot_manual::render((width, height));

    unsafe {
        result.copy_from(res.as_ptr(), res.len());
    }
}

#[ffi_export]
fn load_person(id: i64) -> CPerson {
    log::info!("id: {}", id);

    let person = person::load(id as u64).unwrap();
    CPerson {
        id: person.id,
        name: str::Box::from(format!("{} {}", person.name.first, person.name.last)),
    }
}

#[derive_ReprC]
#[repr(C)]
pub struct CPerson {
    pub id: u64,
    pub name: str::Box,
}
