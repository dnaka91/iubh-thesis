package com.github.dnaka91.bender.kt

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class Person(
    val id: Long,
    val name: Name,
    val gender: Gender,
    val birthday: LocalDate,
    val addresses: List<Address>,
    val weight: Double,
    val totalSteps: Long,
)

sealed class Gender {
    object Female : Gender()
    object Male : Gender()
    data class Other(val value: String) : Gender()

    override fun toString(): String = when (this) {
        is Female -> "Female"
        is Male -> "Male"
        is Other -> "Other($value)"
    }
}

@JsonClass(generateAdapter = true)
data class Name(
    val title: String?,
    val first: String,
    val middle: String?,
    val last: String,
)

@JsonClass(generateAdapter = true)
data class Address(
    val street: String,
    val houseNumber: String,
    val city: String,
    val country: String,
    val postalCode: String,
    val details: List<String>,
)