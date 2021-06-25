package com.github.dnaka91.drinkup.kt.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.dnaka91.drinkup.kt.entities.RecordEntity
import java.time.LocalDate
import java.time.OffsetDateTime

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    suspend fun getAll(): List<RecordEntity>

    @Query("SELECT * FROM history WHERE date(timestamp) = date(:date)")
    suspend fun getByDate(date: OffsetDateTime): List<RecordEntity>

    @Query("SELECT ifnull(sum(amount), 0) FROM history WHERE date(timestamp) = date(:date)")
    suspend fun getProgress(date: OffsetDateTime): Int

    @Insert
    suspend fun insert(record: RecordEntity)
}
