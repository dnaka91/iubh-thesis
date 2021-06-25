use bend::hello;

pub fn hello(name: String) -> String {
    hello::greet(&name)
}
