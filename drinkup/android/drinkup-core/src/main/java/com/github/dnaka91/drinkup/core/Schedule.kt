package com.github.dnaka91.drinkup.core

import java.time.LocalTime

data class Schedule(
    val start: LocalTime,
    val end: LocalTime,
    val goal: Int
)
