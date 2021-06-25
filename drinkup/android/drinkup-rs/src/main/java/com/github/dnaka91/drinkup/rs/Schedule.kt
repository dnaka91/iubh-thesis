package com.github.dnaka91.drinkup.rs

import com.squareup.moshi.JsonClass
import java.time.LocalTime
import com.github.dnaka91.drinkup.core.Schedule as CoreSchedule

@JsonClass(generateAdapter = true)
internal data class Schedule(
    val start: LocalTime,
    val end: LocalTime,
    val goal: Int
) {
    fun toCore(): CoreSchedule = CoreSchedule(start, end, goal)


    companion object {
        fun fromCore(c: CoreSchedule): Schedule = Schedule(c.start, c.end, c.goal)
    }
}
