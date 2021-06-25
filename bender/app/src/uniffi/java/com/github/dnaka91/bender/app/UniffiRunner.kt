package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.uniffi.*


object UniffiRunner : Runner {
    override val features: Set<Feature> = setOf(
        Feature.Empty,
        Feature.Hello,
        Feature.Mandelbrot,
        Feature.MandelbrotManual,
        Feature.LoadPerson,
        Feature.SavePerson,
        Feature.Callback
    )

    override fun empty() = BendUniffi.empty()

    override fun hello(name: String) = BendUniffi.hello(name)

    override fun mandelbrot(width: Int, height: Int) = BendUniffi.mandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) =
        BendUniffi.mandelbrotManual(width, height)

    override fun loadPerson(id: Long) {
        BendUniffi.loadPerson(id).toString()
    }

    override fun savePerson() {
        BendUniffi.savePerson(person())
    }

    override fun loadPersonJson(id: Long) {}

    override fun savePersonJson() {}

    override fun callback() {
        val cb = Callback()
        cb.onComplete(5)
        cb.destroy()
    }

    private fun person() = Person(
        id = 5u,
        name = Name(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = Gender.MALE,
        birthday = SimpleDate(1990, 3u, 14u),
        addresses = listOf(
            Address(
                street = "Sample Street 1-2",
                houseNumber = "33b",
                city = "Sampleton",
                country = "Samplevania",
                postalCode = "111-222",
                details = listOf("Room 5"),
            )
        ),
        weight = 72.5,
        totalSteps = 33095u,
    )
}