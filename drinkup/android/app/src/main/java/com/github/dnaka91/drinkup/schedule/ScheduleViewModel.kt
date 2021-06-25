package com.github.dnaka91.drinkup.schedule

import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val drinkup: DrinkupService
) : ViewModel() {
    private var start = LocalTime.of(0, 0)
    private var end = LocalTime.of(0, 0)
    private var goal = 0

    fun start(): LocalTime = start
    fun end(): LocalTime = end

    fun onStartChanged(value: LocalTime) {
        start = value
    }

    fun onEndChanged(value: LocalTime) {
        end = value
    }

    fun onGoalChanged(value: Int) {
        goal = value
    }

    suspend fun schedule(): Schedule {
        val s = drinkup.schedule()
        start = s.start
        end = s.end
        goal = s.goal
        return s
    }

    suspend fun save(): Boolean {
        if (end <= start || goal <= 0) {
            return false
        }

        drinkup.saveSchedule(Schedule(start, end, goal))
        return true
    }
}
