use bend::{
    mandelbrot::{self, Complex64},
    mandelbrot_manual,
};

pub fn mandelbrot(width: u32, height: u32) -> Vec<u8> {
    mandelbrot::render(
        (width, height),
        Complex64 {
            re: -1.20,
            im: 0.35,
        },
        Complex64 {
            re: -1.00,
            im: 0.20,
        },
    )
}

pub fn mandelbrot_manual(width: u32, height: u32) -> Vec<u8> {
    mandelbrot_manual::render((width, height))
}
