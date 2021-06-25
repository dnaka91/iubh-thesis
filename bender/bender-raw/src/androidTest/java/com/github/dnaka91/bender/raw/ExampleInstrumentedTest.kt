package com.github.dnaka91.bender.raw

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
        assertEquals("com.github.dnaka91.bender.raw.test", appContext.packageName)
    }

    @Test
    fun hello() {
        val answer = BendRaw.hello("Bender")
        assertEquals("Hello, Bender!", answer)
    }

    @Test
    fun mandelbrot() {
        val data = BendRaw.mandelbrot(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun mandelbrotManual() {
        val data = BendRaw.mandelbrotManual(10, 10)
        assertEquals(100, data.size)
    }

    @Test
    fun loadPerson() {
        val person = BendRaw.loadPerson(5)
        Log.e("PERSON", person.toString())
    }

    @Test
    fun loadPersonJson() {
        val person = BendRaw.loadPersonJson(5)
        Log.e("PERSON", person.toString())
    }

    @Test
    fun loadPersonDirect() {
        val person = BendRaw.loadPersonDirect(5)
        Log.e("PERSON", person.toString())
    }

    @Test
    fun callback() {
        BendRaw.callback().invoke(5)
    }
}