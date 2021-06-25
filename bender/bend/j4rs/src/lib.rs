#![deny(rust_2018_idioms, clippy::all)]
#![allow(non_fmt_panic)]

use std::convert::TryFrom;

use bend::{
    hello,
    mandelbrot::{self, Complex64},
};
use j4rs::{prelude::*, InvocationArg};
use j4rs_derive::call_from_java;

#[call_from_java("com.github.dnaka91.bender.j4rs.BendJ4rs.empty")]
fn empty() {}

#[call_from_java("com.github.dnaka91.bender.j4rs.BendJ4rs.hello")]
fn hello(name: Instance) -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();
    let name = jvm.to_rust::<String>(name).unwrap();

    let output = InvocationArg::try_from(hello::greet(&name)).map_err(|e| e.to_string())?;

    Instance::try_from(output).map_err(|e| e.to_string())
}

#[call_from_java("com.github.dnaka91.bender.j4rs.BendJ4rs.mandelbrot")]
fn mandelbrot(width: Instance, height: Instance) -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();
    let width = jvm.to_rust::<u32>(width).unwrap();
    let height = jvm.to_rust::<u32>(height).unwrap();

    let result = mandelbrot::render(
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
    let result = result.into_iter().map(|b| b as i8).collect::<Vec<_>>();

    Instance::try_from(InvocationArg::try_from(&result[..]).map_err(|e| e.to_string())?)
        .map_err(|e| e.to_string())
}
