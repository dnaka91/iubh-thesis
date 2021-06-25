use jni::{objects::JClass, JNIEnv};

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_empty(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
}
