package com.github.dnaka91.bender.safer_ffi

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
        assertEquals("com.github.dnaka91.bender.safer_ffi.test", appContext.packageName)
    }

    @Test
    fun hello() {
        val answer = BendSaferFfi.hello("Bender")
        assertEquals("Hello, Bender!", answer)
    }

    @Test
    fun mandelbrot() {
        val data = BendSaferFfi.mandelbrot(10, 10)
        assertEquals(100, data.size)
    }

    // Crashes with a native error
    /*@Test
    fun loadPerson() {
        val person = BendSaferFfi.loadPerson(5)
        assertEquals("CPerson{id=5,name='Max Mustermann'}", person.toString())
    }*/
}