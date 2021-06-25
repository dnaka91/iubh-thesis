use bend::{
    mandelbrot::{self, Complex64},
    mandelbrot_manual,
};

#[no_mangle]
pub unsafe extern "C" fn bend_mandelbrot(width: u32, height: u32, result: *mut u8) {
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

    result.copy_from(res.as_ptr(), res.len());
}

#[no_mangle]
pub unsafe extern "C" fn bend_mandelbrot_manual(width: u32, height: u32, result: *mut u8) {
    let res = mandelbrot_manual::render((width, height));

    result.copy_from(res.as_ptr(), res.len());
}
