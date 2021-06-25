package com.github.dnaka91.drinkup.kt

import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.IntakeSize
import com.github.dnaka91.drinkup.core.Record
import com.github.dnaka91.drinkup.core.Schedule
import com.github.dnaka91.drinkup.kt.entities.IntakeSizeEntity
import java.time.*
import java.time.format.DateTimeFormatter
import com.github.dnaka91.drinkup.kt.entities.RecordEntity.Companion as RecordEntity

class Drinkup(
    private val db: AppDatabase,
    private val schedulePrefs: SchedulePreferences,
) : DrinkupService {
    override suspend fun schedule(): Schedule = Schedule(
        LocalTime.parse(schedulePrefs.start, DateTimeFormatter.ISO_LOCAL_TIME),
        LocalTime.parse(schedulePrefs.end, DateTimeFormatter.ISO_LOCAL_TIME),
        schedulePrefs.goal
    )

    override suspend fun saveSchedule(schedule: Schedule) {
        schedulePrefs.start = schedule.start.format(DateTimeFormatter.ISO_LOCAL_TIME)
        schedulePrefs.end = schedule.end.format(DateTimeFormatter.ISO_LOCAL_TIME)
        schedulePrefs.goal = schedule.goal
    }

    override suspend fun listIntakeSizes(): List<IntakeSize> =
        db.intakeSizeDao().getAll().map { it.toCore() }

    override suspend fun saveIntakeSize(size: IntakeSize) {
        db.intakeSizeDao().upsert(IntakeSizeEntity.fromCore(size))
    }

    override suspend fun history(date: LocalDate): List<Record> =
        db.historyDao().getByDate(date.atTime(OffsetTime.now())).map { it.toCore() }


    override suspend fun addRecord(record: Record) {
        db.historyDao().insert(RecordEntity.fromCore(record))
    }

    override suspend fun progress(): Int {
        return db.historyDao().getProgress(OffsetDateTime.now())
    }

    override suspend fun nextAlarm(): Duration? = Duration.ofMinutes(30)

    override suspend fun drinkAmount(): Int = 250
}
