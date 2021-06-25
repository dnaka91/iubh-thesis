package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.j4rs.BendJ4rs

object J4rsRunner : Runner {
    override val features: Set<Feature> = setOf(
        Feature.Empty,
        Feature.Hello,
        Feature.Mandelbrot
    )

    override fun empty() = BendJ4rs.callEmpty()

    override fun hello(name: String) = BendJ4rs.callHello(name)

    override fun mandelbrot(width: Int, height: Int) = BendJ4rs.callMandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) = byteArrayOf()

    override fun loadPerson(id: Long) {}

    override fun savePerson() {}

    override fun loadPersonJson(id: Long) {}

    override fun savePersonJson() {}

    override fun callback() {}
}
