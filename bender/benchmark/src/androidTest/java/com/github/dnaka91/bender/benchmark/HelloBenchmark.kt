package com.github.dnaka91.bender.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.dnaka91.bender.jni.BendJni
import com.github.dnaka91.bender.kt.BendKt
import com.github.dnaka91.bender.raw.BendRaw
import com.github.dnaka91.bender.safer_ffi.BendSaferFfi
import com.github.dnaka91.bender.uniffi.BendUniffi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelloBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun jni() = benchmarkRule.measureRepeated {
        BendJni.hello("Bender")
    }

    @Test
    fun uniffi() = benchmarkRule.measureRepeated {
        BendUniffi.hello("Bender")
    }

    @Test
    fun raw() = benchmarkRule.measureRepeated {
        BendRaw.hello("Bender")
    }

    @Test
    fun saferFfi() = benchmarkRule.measureRepeated {
        BendSaferFfi.hello("Bender")
    }

    @Test
    fun kt() = benchmarkRule.measureRepeated {
        BendKt.hello("Bender")
    }
}