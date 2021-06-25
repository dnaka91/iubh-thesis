package com.github.dnaka91.drinkup.rs

import com.github.dnaka91.drinkup.core.DrinkupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import com.github.dnaka91.drinkup.core.IntakeSize as CoreIntakeSize
import com.github.dnaka91.drinkup.core.Record as CoreRecord
import com.github.dnaka91.drinkup.core.Schedule as CoreSchedule

object Drinkup {
    init {
        System.loadLibrary("drinkup_java")
        init()
    }

    private external fun init()

    private external fun create(baseDir: String): Long

    private external fun destroy(ptr: Long)

    @Synchronized
    private external fun scheduleJson(ptr: Long): String

    @Synchronized
    private external fun saveScheduleJson(ptr: Long, schedule: String)

    @Synchronized
    private external fun listIntakeSizesJson(ptr: Long): String

    @Synchronized
    private external fun saveIntakeSizeJson(ptr: Long, size: String)

    @Synchronized
    private external fun historyJson(ptr: Long, timestamp: Long): String

    @Synchronized
    private external fun addRecordJson(ptr: Long, record: String)

    @Synchronized
    private external fun progress(ptr: Long): Int

    @Synchronized
    private external fun nextAlarm(ptr: Long): Long?

    @Synchronized
    private external fun drinkAmount(ptr: Long): Int

    class Instance private constructor(private val ptr: Long) : DrinkupService {
        constructor(baseDir: String) : this(create(baseDir))

        protected fun finalize() {
            destroy(ptr)
        }

        override suspend fun schedule(): CoreSchedule = withContext(Dispatchers.IO) {
            scheduleJson(ptr).let {
                Json.schedule.fromJson(it)!!.toCore()
            }
        }

        override suspend fun saveSchedule(schedule: CoreSchedule) {
            withContext(Dispatchers.IO) {
                Json.schedule.toJson(Schedule.fromCore(schedule)).let {
                    saveScheduleJson(ptr, it)
                }
            }
        }

        override suspend fun listIntakeSizes(): List<CoreIntakeSize> = withContext(Dispatchers.IO) {
            listIntakeSizesJson(ptr).let {
                Json.intakeSizeList.fromJson(it)!!.map(IntakeSize::toCore)
            }
        }

        override suspend fun saveIntakeSize(size: CoreIntakeSize) {
            withContext(Dispatchers.IO) {
                Json.intakeSize.toJson(IntakeSize.fromCore(size)).let {
                    saveIntakeSizeJson(ptr, it)
                }
            }
        }

        override suspend fun history(date: LocalDate): List<CoreRecord> =
            withContext(Dispatchers.IO) {
                historyJson(ptr, date.toEpochDay()).let {
                    Json.recordList.fromJson(it)!!.map(Record::toCore)
                }
            }

        override suspend fun addRecord(record: CoreRecord) {
            withContext(Dispatchers.IO) {
                Json.record.toJson(Record.fromCore(record)).let {
                    addRecordJson(ptr, it)
                }
            }
        }

        override suspend fun progress(): Int = withContext(Dispatchers.IO) { progress(ptr) }

        override suspend fun nextAlarm(): Duration? = withContext(Dispatchers.IO) {
            nextAlarm(ptr)?.let {
                Duration.ofMinutes(it)
            }
        }

        override suspend fun drinkAmount(): Int = withContext(Dispatchers.IO) {
            drinkAmount(ptr)
        }
    }
}
