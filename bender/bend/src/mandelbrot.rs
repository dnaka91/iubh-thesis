pub use num_complex::Complex64;
use num_traits::PrimInt;

fn escape<T: PrimInt>(c: Complex64, limit: T) -> T {
    let mut z = Complex64 { re: 0.0, im: 0.0 };
    for i in num_iter::range(T::zero(), limit) {
        z = z * z + c;
        if z.norm_sqr() > 4.0 {
            return i;
        }
    }

    limit
}

fn px_to_point(
    bounds: (u32, u32),
    pixel: (u32, u32),
    upper_left: Complex64,
    lower_right: Complex64,
) -> Complex64 {
    let (width, height) = (
        lower_right.re - upper_left.re,
        upper_left.im - lower_right.im,
    );

    Complex64 {
        re: upper_left.re + pixel.0 as f64 * width / bounds.0 as f64,
        im: upper_left.im - pixel.1 as f64 * height / bounds.1 as f64,
    }
}

/// Render a mandelbrot image with the given bounds and coordinates into a vector of bytes. Each
/// byte represents a grayscale pixel.
pub fn render(bounds: (u32, u32), upper_left: Complex64, lower_right: Complex64) -> Vec<u8> {
    let mut pixels = vec![0; (bounds.0 * bounds.1) as usize];

    for row in 0..bounds.1 {
        for column in 0..bounds.0 {
            let point = px_to_point(bounds, (column, row), upper_left, lower_right);

            pixels[(row * bounds.0 + column) as usize] = 255 - escape(point, 255)
        }
    }

    pixels
}
