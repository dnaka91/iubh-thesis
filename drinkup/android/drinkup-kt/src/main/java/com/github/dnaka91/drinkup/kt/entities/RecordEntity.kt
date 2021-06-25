package com.github.dnaka91.drinkup.kt.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.dnaka91.drinkup.core.Record
import java.time.OffsetDateTime

@Entity(tableName = "history")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timestamp: OffsetDateTime,
    val name: String,
    val amount: Int
) {
    fun toCore(): Record = Record(id, timestamp, name, amount)

    companion object {
        fun fromCore(r: Record) = RecordEntity(r.id, r.timestamp, r.name, r.amount)
    }
}
