use num_traits::PrimInt;

#[derive(Clone, Copy)]
struct Pair(f64, f64);

fn escape<T: PrimInt>(c: Pair, limit: T) -> T {
    let mut z = Pair(0.0, 0.0);

    for i in num_iter::range(T::zero(), limit) {
        let ac = z.0 * z.0;
        let bd = z.1 * z.1;
        let ad = z.0 * z.1;
        let bc = z.1 * z.0;

        z.0 = ac - bd + c.0;
        z.1 = ad + bc + c.1;

        if z.0 * z.0 + z.1 * z.1 > 4.0 {
            return i;
        }
    }

    limit
}

fn px_to_point(bounds: (u32, u32), pixel: (u32, u32), upper_left: Pair, lower_right: Pair) -> Pair {
    let (width, height) = (lower_right.0 - upper_left.0, upper_left.1 - lower_right.1);

    Pair(
        upper_left.0 + pixel.0 as f64 * width / bounds.0 as f64,
        upper_left.1 - pixel.1 as f64 * height / bounds.1 as f64,
    )
}

pub fn render(bounds: (u32, u32)) -> Vec<u8> {
    let mut pixels = vec![0; (bounds.0 * bounds.1) as usize];
    let upper_left = Pair(-1.2, 0.35);
    let lower_right = Pair(-1.0, 0.2);

    for row in 0..bounds.1 {
        for column in 0..bounds.0 {
            let point = px_to_point(bounds, (column, row), upper_left, lower_right);

            pixels[(row * bounds.0 + column) as usize] = 255 - escape(point, 255)
        }
    }

    pixels
}
