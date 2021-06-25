package com.github.dnaka91.drinkup

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.github.dnaka91.drinkup.core.IntakeSize
import com.github.dnaka91.drinkup.core.Schedule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
class DrinkupServiceTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var drinkup: DrinkupServiceFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun cleanUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.filesDir.absoluteFile.deleteRecursively()
        context.deleteDatabase("data")
        context.getSharedPreferences("schedule", 0).edit(true) { clear() }
    }

    @Test
    fun getSchedule() = runBlocking {
        val schedule = drinkup.create().schedule()
        assertThat(schedule).isEqualTo(
            Schedule(
                start = LocalTime.of(8, 0),
                end = LocalTime.of(22, 0),
                goal = 2400,
            )
        )
    }

    @Test
    fun setSchedule() = runBlocking {
        val schedule = Schedule(
            start = LocalTime.of(4, 0),
            end = LocalTime.of(18, 0),
            goal = 100,
        )
        val drinkup = drinkup.create()
        drinkup.saveSchedule(schedule)
        assertThat(drinkup.schedule()).isEqualTo(schedule)
    }

    @Test
    fun getIntakeSizes() = runBlocking {
        val intakeSizes = drinkup.create().listIntakeSizes()
        assertThat(intakeSizes).containsExactly(
            IntakeSize(1, "Small Cup", 150),
            IntakeSize(2, "Big Cup", 250),
            IntakeSize(3, "Bottle", 500),
        )
    }

    @Test
    fun saveIntakeSize() = runBlocking {
        val drinkup = drinkup.create()
        drinkup.saveIntakeSize(IntakeSize(2, "Big Bottle", 2500))
        drinkup.saveIntakeSize(IntakeSize(10, "Tiny Drop", 1))
        assertThat(drinkup.listIntakeSizes()).containsExactly(
            IntakeSize(1, "Small Cup", 150),
            IntakeSize(2, "Big Bottle", 2500),
            IntakeSize(3, "Bottle", 500),
            IntakeSize(10, "Tiny Drop", 1)
        )
    }

    @Test
    fun getHistory() = runBlocking {
        val history = drinkup.create().history(LocalDate.now())
        assertThat(history).isEmpty()
    }

    @Test
    fun getProgress() = runBlocking {
        val progress = drinkup.create().progress()
        assertThat(progress).isEqualTo(0)
    }

    @Test
    fun getNextAlarm() = runBlocking<Unit> {
        drinkup.create().nextAlarm()
    }

    @Test
    fun getDrinkAmount() = runBlocking {
        val drinkAmount = drinkup.create().drinkAmount()
        assertThat(drinkAmount).isGreaterThan(0)
    }
}
