package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.safer_ffi.BendSaferFfi

object SaferffiRunner : Runner {
    override val features: Set<Feature> = setOf(
        Feature.Empty,
        Feature.Hello,
        Feature.Mandelbrot
    )

    override fun empty() = BendSaferFfi.empty()

    override fun hello(name: String) = BendSaferFfi.hello(name)

    override fun mandelbrot(width: Int, height: Int) = BendSaferFfi.mandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) = byteArrayOf()

    override fun loadPerson(id: Long) {}

    override fun savePerson() {}

    override fun loadPersonJson(id: Long) {}

    override fun savePersonJson() {}

    override fun callback() {}
}
