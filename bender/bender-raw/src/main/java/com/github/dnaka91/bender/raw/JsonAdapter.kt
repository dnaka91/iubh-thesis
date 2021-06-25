package com.github.dnaka91.bender.raw

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal object JsonAdapter {
    private val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .add(GenderAdapter())
        .build()
    internal val person = moshi.adapter(Person::class.java)

    class GenderAdapter {
        @ToJson
        fun toJson(gender: Gender): String = when (gender) {
            is Gender.Female -> "Female"
            is Gender.Male -> "Male"
            is Gender.Other -> gender.value
        }

        @FromJson
        fun fromJson(gender: String): Gender = when (gender) {
            "Female" -> Gender.Female
            "Male" -> Gender.Male
            else -> Gender.Other(gender)
        }
    }

    class LocalDateAdapter {
        @ToJson
        fun toJson(localDate: LocalDate): String =
            localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        @FromJson
        fun fromJson(localDate: String): LocalDate =
            LocalDate.parse(localDate, DateTimeFormatter.ISO_LOCAL_DATE)
    }
}