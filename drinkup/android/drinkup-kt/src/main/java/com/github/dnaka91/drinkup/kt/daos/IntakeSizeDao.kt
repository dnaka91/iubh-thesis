package com.github.dnaka91.drinkup.kt.daos

import androidx.room.*
import com.github.dnaka91.drinkup.kt.entities.IntakeSizeEntity

@Dao
interface IntakeSizeDao {
    @Query("SELECT * FROM intake_sizes")
    suspend fun getAll(): List<IntakeSizeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(size: IntakeSizeEntity): Long

    @Update
    suspend fun update(size: IntakeSizeEntity)

    @Transaction
    suspend fun upsert(size: IntakeSizeEntity) {
        if (insert(size) == -1L) {
            update(size)
        }
    }
}
