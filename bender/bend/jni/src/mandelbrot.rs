use bend::{
    mandelbrot::{self, Complex64},
    mandelbrot_manual,
};
use jni::{
    objects::JClass,
    sys::{jbyteArray, jint},
    JNIEnv,
};

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_mandelbrot(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    width: jint,
    height: jint,
) -> jbyteArray {
    assert!(width >= 0 && height >= 0);

    let result = mandelbrot::render(
        (width as u32, height as u32),
        Complex64 {
            re: -1.20,
            im: 0.35,
        },
        Complex64 {
            re: -1.00,
            im: 0.20,
        },
    );

    env.byte_array_from_slice(&result).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_com_github_dnaka91_bender_jni_BendJni_mandelbrotManual(
    env: JNIEnv<'_>,
    _class: JClass<'_>,
    width: jint,
    height: jint,
) -> jbyteArray {
    assert!(width >= 0 && height >= 0);

    let result = mandelbrot_manual::render((width as u32, height as u32));

    env.byte_array_from_slice(&result).unwrap()
}
