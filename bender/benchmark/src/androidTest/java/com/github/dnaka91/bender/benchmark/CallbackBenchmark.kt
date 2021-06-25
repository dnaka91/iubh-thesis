package com.github.dnaka91.bender.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.dnaka91.bender.jni.BendJni
import com.github.dnaka91.bender.kt.BendKt
import com.github.dnaka91.bender.raw.BendRaw
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.github.dnaka91.bender.uniffi.Callback as UniffiCallback

@RunWith(AndroidJUnit4::class)
class CallbackBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun jni() = benchmarkRule.measureRepeated {
        BendJni.callback {
            assert(it == 5)
        }
    }

    @Test
    fun uniffi() = benchmarkRule.measureRepeated {
        val cb = UniffiCallback()
        cb.onComplete(5)
        cb.destroy()
    }

    @Test
    fun raw() = benchmarkRule.measureRepeated {
        BendRaw.callback().invoke(5)
    }

    @Test
    fun kt() = benchmarkRule.measureRepeated {
        BendKt.callback {
            assert(it == 5)
        }
    }
}