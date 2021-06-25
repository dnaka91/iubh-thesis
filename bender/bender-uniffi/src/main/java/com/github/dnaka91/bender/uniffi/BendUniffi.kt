package com.github.dnaka91.bender.uniffi

import com.github.dnaka91.bender.uniffi.empty as uniffiEmpty
import com.github.dnaka91.bender.uniffi.hello as uniffiHello
import com.github.dnaka91.bender.uniffi.init as uniffiInit
import com.github.dnaka91.bender.uniffi.loadPerson as uniffiLoadPerson
import com.github.dnaka91.bender.uniffi.mandelbrot as uniffiMandelbrot
import com.github.dnaka91.bender.uniffi.mandelbrotManual as uniffiMandelbrotManual
import com.github.dnaka91.bender.uniffi.savePerson as uniffiSavePerson

object BendUniffi {
    init {
        uniffiInit()
    }

    fun empty() = uniffiEmpty()

    fun hello(name: String): String = uniffiHello(name)

    fun mandelbrot(width: Int, height: Int): ByteArray =
        uniffiMandelbrot(width.toUInt(), height.toUInt()).toUByteArray().toByteArray()

    fun mandelbrotManual(width: Int, height: Int): ByteArray =
        uniffiMandelbrotManual(width.toUInt(), height.toUInt()).toUByteArray().toByteArray()

    fun loadPerson(id: Long): Person = uniffiLoadPerson(id.toULong())

    fun savePerson(person: Person) {
        uniffiSavePerson(person)
    }
}