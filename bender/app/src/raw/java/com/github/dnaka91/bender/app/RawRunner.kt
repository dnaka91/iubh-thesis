package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.raw.BendRaw

object RawRunner : Runner {
    override val features: Set<Feature> = setOf(
        Feature.Empty,
        Feature.Hello,
        Feature.Mandelbrot,
        Feature.MandelbrotManual,
        Feature.LoadPerson,
        Feature.Callback
    )

    override fun empty() = BendRaw.empty()

    override fun hello(name: String) = BendRaw.hello(name)

    override fun mandelbrot(width: Int, height: Int) = BendRaw.mandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) = BendRaw.mandelbrotManual(width, height)

    override fun loadPerson(id: Long) {
        BendRaw.loadPerson(id).toString()
    }

    override fun savePerson() {}

    override fun loadPersonJson(id: Long) {}

    override fun savePersonJson() {}

    override fun callback() {
        BendRaw.callback().invoke(5)
    }
}
