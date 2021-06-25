#[derive(Default)]
pub struct Callback {}

impl Callback {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn on_complete(&self, result: i32) {
        assert!(result == 5);
    }
}
