package com.github.dnaka91.drinkup.core

import java.time.OffsetDateTime

data class Record(
    val id: Int,
    val timestamp: OffsetDateTime,
    val name: String,
    val amount: Int
)

