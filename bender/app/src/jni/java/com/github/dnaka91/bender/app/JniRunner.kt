package com.github.dnaka91.bender.app

import com.github.dnaka91.bender.jni.*
import java.time.LocalDate

object JniRunner : Runner {
    override val features: Set<Feature> = Feature.values().toSet()

    override fun empty() = BendJni.empty()

    override fun hello(name: String) = BendJni.hello(name)

    override fun mandelbrot(width: Int, height: Int) = BendJni.mandelbrot(width, height)

    override fun mandelbrotManual(width: Int, height: Int) = BendJni.mandelbrotManual(width, height)

    override fun loadPerson(id: Long) {
        BendJni.loadPerson(id).toString()
    }

    override fun savePerson() {
        BendJni.savePerson(jniPerson())
    }

    override fun loadPersonJson(id: Long) {
        BendJni.loadPersonJson2(id).toString()
    }

    override fun savePersonJson() {
        BendJni.savePersonJson2(jniPerson())
    }

    override fun callback() {
        BendJni.callback {
            if (BuildConfig.DEBUG && it != 5) {
                error("Assertion failed")
            }
        }
    }

    private fun jniPerson() = Person(
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
                details = listOf("Room 5"),
            )
        ),
        weight = 72.5,
        totalSteps = 33095,
    )
}
