package com.github.dnaka91.drinkup.core

import java.time.LocalDate
import java.time.Duration

interface DrinkupService {
    suspend fun schedule(): Schedule
    suspend fun saveSchedule(schedule: Schedule)
    suspend fun listIntakeSizes(): List<IntakeSize>
    suspend fun saveIntakeSize(size: IntakeSize)
    suspend fun history(date: LocalDate): List<Record>
    suspend fun addRecord(record: Record)
    suspend fun progress(): Int
    suspend fun nextAlarm(): Duration?
    suspend fun drinkAmount(): Int
}
