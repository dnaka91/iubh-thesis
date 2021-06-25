package com.github.dnaka91.bender.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.dnaka91.bender.jni.BendJni
import com.github.dnaka91.bender.kt.BendKt
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import com.github.dnaka91.bender.jni.Address as JniAddress
import com.github.dnaka91.bender.jni.Gender as JniGender
import com.github.dnaka91.bender.jni.Name as JniName
import com.github.dnaka91.bender.jni.Person as JniPerson
import com.github.dnaka91.bender.kt.Address as KtAddress
import com.github.dnaka91.bender.kt.Gender as KtGender
import com.github.dnaka91.bender.kt.Name as KtName
import com.github.dnaka91.bender.kt.Person as KtPerson
import com.github.dnaka91.bender.uniffi.Address as UniffiAddress
import com.github.dnaka91.bender.uniffi.Gender as UniffiGender
import com.github.dnaka91.bender.uniffi.Name as UniffiName
import com.github.dnaka91.bender.uniffi.Person as UniffiPerson
import com.github.dnaka91.bender.uniffi.SimpleDate as UniffiSimpleDate

@RunWith(AndroidJUnit4::class)
class SavePersonJsonBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private fun jniPerson() = JniPerson(
        id = 5,
        name = JniName(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = JniGender.Male,
        birthday = LocalDate.of(1990, 3, 14),
        addresses = listOf(
            JniAddress(
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

    private fun uniffiPerson() = UniffiPerson(
        id = 5u,
        name = UniffiName(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = UniffiGender.MALE,
        birthday = UniffiSimpleDate(1990, 3u, 14u),
        addresses = listOf(
            UniffiAddress(
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

    private fun ktPerson() = KtPerson(
        id = 5,
        name = KtName(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = KtGender.Male,
        birthday = LocalDate.of(1990, 3, 14),
        addresses = listOf(
            KtAddress(
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
    fun jni() = benchmarkRule.measureRepeated {
        BendJni.savePersonJson2(jniPerson())
    }

    @Test
    fun kt() = benchmarkRule.measureRepeated {
        BendKt.savePersonJson(ktPerson())
    }
}