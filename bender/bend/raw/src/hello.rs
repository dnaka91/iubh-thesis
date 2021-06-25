use std::{
    ffi::{CStr, CString},
    mem,
    os::raw::c_char,
};

use bend::hello;

#[no_mangle]
pub unsafe extern "C" fn bend_hello(name: *const c_char) -> *const c_char {
    let name = CString::from(CStr::from_ptr(name)).into_string().unwrap();
    let result = CString::new(hello::greet(&name)).unwrap();

    let p = result.as_ptr();
    mem::forget(result);

    p
}
