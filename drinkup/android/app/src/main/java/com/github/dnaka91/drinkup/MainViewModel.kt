package com.github.dnaka91.drinkup

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.IntakeSize
import com.github.dnaka91.drinkup.core.Record
import com.github.dnaka91.drinkup.core.Schedule
import com.github.dnaka91.drinkup.extensions.scheduleNextAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val drinkup: DrinkupService
) : ViewModel() {
    suspend fun schedule(): Schedule = drinkup.schedule()

    suspend fun intakeSizes(): List<IntakeSize> = drinkup.listIntakeSizes()

    suspend fun progress(): Int = drinkup.progress()

    suspend fun progressText(context: Context): String {
        val schedule = drinkup.schedule()
        val progress = drinkup.progress()

        return context.getString(
            R.string.goal_progress,
            progress,
            schedule.goal,
            progress * 100 / schedule.goal
        )
    }

    suspend fun addRecord(name: String, amount: Int) {
        drinkup.addRecord(Record(0, OffsetDateTime.now(), name, amount))
    }

    suspend fun scheduleAlarm(context: Context) {
        context.scheduleNextAlarm(drinkup)
    }
}
