package com.github.dnaka91.bender.uniffi

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

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
        assertEquals("com.github.dnaka91.bender.uniffi.test", appContext.packageName)
    }

    @Test
    fun hello() {
        val answer = BendUniffi.hello("Bender")
        assertEquals("Hello, Bender!", answer)
    }

    @Test
    fun mandelbrot() {
        val data = BendUniffi.mandelbrot(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun mandelbrotManual() {
        val data = BendUniffi.mandelbrotManual(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun loadPerson() {
        val person = BendUniffi.loadPerson(5)
        Log.e("PERSON", person.toString())
    }

    private val person = Person(
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

    @Test
    fun savePerson() {
        BendUniffi.savePerson(person)
    }

    @Test
    fun callback() {
        val cb = Callback()
        cb.onComplete(5)
        cb.destroy()
    }
}