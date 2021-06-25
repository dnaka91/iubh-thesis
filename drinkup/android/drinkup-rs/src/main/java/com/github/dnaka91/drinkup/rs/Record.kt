package com.github.dnaka91.drinkup.rs

import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime
import com.github.dnaka91.drinkup.core.Record as CoreRecord

@JsonClass(generateAdapter = true)
internal data class Record(
    val id: Int,
    val timestamp: OffsetDateTime,
    val name: String,
    val amount: Int
) {
    fun toCore(): CoreRecord = CoreRecord(id, timestamp, name, amount)

    companion object {
        fun fromCore(c: CoreRecord): Record = Record(c.id, c.timestamp, c.name, c.amount)
    }
}
