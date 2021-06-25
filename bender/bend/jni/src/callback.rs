use jni::{
    objects::{JClass, JObject},
    JNIEnv,
};

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_callback(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    callback: JObject<'_>,
) {
    env.call_method(callback, "onComplete", "(I)V", &[5.into()])
        .unwrap();
}
