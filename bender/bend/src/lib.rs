#![deny(rust_2018_idioms, clippy::all)]

pub mod hello;
pub mod mandelbrot;
pub mod mandelbrot_manual;
pub mod person;

#[cfg(test)]
mod tests {
    use num_complex::Complex64;

    use super::*;

    #[test]
    fn mandel() {
        let m1 = mandelbrot::render(
            (100, 100),
            Complex64::new(-1.2, 0.35),
            Complex64::new(-1.0, 0.2),
        );
        let m2 = mandelbrot_manual::render((100, 100));

        assert_eq!(m1, m2);
    }
}
