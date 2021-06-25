package com.github.dnaka91.bender.j4rs

import org.astonbitecode.j4rs.api.Instance
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils

object BendJ4rs {
    init {
        System.loadLibrary("bend_j4rs")
    }

    private external fun empty()

    private external fun hello(name: Instance<String>): Instance<*>

    private external fun mandelbrot(
        width: Instance<Int>,
        height: Instance<Int>
    ): Instance<*>

    fun callEmpty() = empty()

    fun callHello(name: String): String {
        val instance = hello(Java2RustUtils.createInstance(name))

        return Java2RustUtils.getObjectCasted(instance)
    }

    fun callMandelbrot(width: Int, height: Int): ByteArray {
        val instance = mandelbrot(
            Java2RustUtils.createInstance(width),
            Java2RustUtils.createInstance(height)
        )

        return Java2RustUtils.getObjectCasted(instance)
    }
}