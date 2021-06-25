package com.github.dnaka91.drinkup.rs

import com.squareup.moshi.JsonClass
import com.github.dnaka91.drinkup.core.IntakeSize as CoreIntakeSize

@JsonClass(generateAdapter = true)
internal data class IntakeSize(
    val id: Int,
    val name: String,
    val amount: Int
) {
    fun toCore(): CoreIntakeSize = CoreIntakeSize(id, name, amount)

    companion object {
        fun fromCore(c: CoreIntakeSize): IntakeSize = IntakeSize(c.id, c.name, c.amount)
    }
}
