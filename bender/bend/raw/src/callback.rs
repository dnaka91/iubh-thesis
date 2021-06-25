#[no_mangle]
pub unsafe extern "C" fn callback() -> *const &'static dyn Fn(i32) {
    cb as *const &dyn Fn(i32)
}

fn cb(result: i32) {
    assert!(result == 5)
}
