use bend::hello;
use jni::{
    objects::{JClass, JString},
    sys::jstring,
    JNIEnv,
};

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_hello(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    name: JString<'_>,
) -> jstring {
    let name = env.get_string(name).unwrap();
    let output = env
        .new_string(hello::greet(name.to_str().unwrap()))
        .unwrap();

    output.into_inner()
}
