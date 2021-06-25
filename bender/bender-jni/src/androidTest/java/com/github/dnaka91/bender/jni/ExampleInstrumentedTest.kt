package com.github.dnaka91.bender.jni

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.github.dnaka91.bender.jni.test", appContext.packageName)
    }

    @Test
    fun hello() {
        val answer = BendJni.hello("Bender")
        assertEquals("Hello, Bender!", answer)
    }

    @Test
    fun mandelbrot() {
        val data = BendJni.mandelbrot(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun mandelbrotManual() {
        val data = BendJni.mandelbrotManual(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun loadPerson() {
        val person = BendJni.loadPerson(5)
        Log.e("PERSON", person.toString())
    }

    @Test
    fun loadPersonJson() {
        val person = BendJni.loadPersonJson2(5)
        Log.e("PERSON", person.toString())
    }

    private val person = Person(
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

    @Test
    fun savePerson() {
        BendJni.savePerson(person)
    }

    @Test
    fun savePersonJson() {
        BendJni.savePersonJson2(person)
    }

    @Test
    fun callback() {
        BendJni.callback { assertEquals(5, it) }
    }
}