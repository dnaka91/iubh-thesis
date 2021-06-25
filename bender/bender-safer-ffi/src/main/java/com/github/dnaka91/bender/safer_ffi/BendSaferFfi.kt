package com.github.dnaka91.bender.safer_ffi

import com.sun.jna.Library
import com.sun.jna.Native

object BendSaferFfi {
    private interface BendLibrary : Library {
        companion object {
            var INSTANCE = Native.load("bend_safer_ffi", BendLibrary::class.java) as BendLibrary

            init {
                INSTANCE.init()
            }
        }

        fun init()

        fun empty()

        fun hello(name: String): String

        fun mandelbrot(width: Int, height: Int, result: ByteArray)

        fun mandelbrot_manual(width: Int, height: Int, result: ByteArray)

        fun load_person(id: Long): CPerson
    }

    fun empty() = BendLibrary.INSTANCE.empty()

    fun hello(name: String): String = BendLibrary.INSTANCE.hello(name)

    fun mandelbrot(width: Int, height: Int): ByteArray = ByteArray(width * height).also {
        BendLibrary.INSTANCE.mandelbrot(width, height, it)
    }

    fun mandelbrotManual(width: Int, height: Int): ByteArray = ByteArray(width * height).also {
        BendLibrary.INSTANCE.mandelbrot_manual(width, height, it)
    }

    fun loadPerson(id: Long): CPerson = BendLibrary.INSTANCE.load_person(id)
}