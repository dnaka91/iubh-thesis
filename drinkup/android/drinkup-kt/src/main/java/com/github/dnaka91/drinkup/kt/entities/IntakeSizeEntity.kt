package com.github.dnaka91.drinkup.kt.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.dnaka91.drinkup.core.IntakeSize

@Entity(tableName = "intake_sizes")
data class IntakeSizeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val amount: Int
) {
    fun toCore(): IntakeSize = IntakeSize(id, name, amount)

    companion object {
        fun fromCore(s: IntakeSize): IntakeSizeEntity = IntakeSizeEntity(s.id, s.name, s.amount)
    }
}
