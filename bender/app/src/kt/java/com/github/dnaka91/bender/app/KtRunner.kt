package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.kt.*
import java.time.LocalDate

object KtRunner : Runner {
    override val features: Set<Feature> = Feature.values().toSet()

    override fun empty() = BendKt.empty()

    override fun hello(name: String) = BendKt.hello(name)

    override fun mandelbrot(width: Int, height: Int) = BendKt.mandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) = BendKt.mandelbrotManual(width, height)

    override fun loadPerson(id: Long) {
        println(BendKt.loadPerson(id).toString())
    }

    override fun savePerson() {
        BendKt.savePerson(ktPerson())
    }

    override fun loadPersonJson(id: Long) {
        println(BendKt.loadPersonJson(id).toString())
    }

    override fun savePersonJson() {
        BendKt.savePersonJson(ktPerson())
    }

    override fun callback() = BendKt.callback {
        if (it != 5) {
            throw RuntimeException("Assertion failed")
        }
    }

    private fun ktPerson() = Person(
        id = 5,
        name = Name(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = Gender.Male,
        birthday = LocalDate.of(1990, 3, 14),
        addresses = listOf(
            Address(
                street = "Sample Street 1-2",
                houseNumber = "33b",
                city = "Sampleton",
                country = "Samplevania",
                postalCode = "111-222",
                details = listOf("Room 5")
            )
        ),
        weight = 72.5,
        totalSteps = 33095,
    )
}
