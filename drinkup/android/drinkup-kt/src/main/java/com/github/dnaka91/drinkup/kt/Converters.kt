package com.github.dnaka91.drinkup.kt

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun offsetDateTimeToString(odt: OffsetDateTime?): String? =
        odt?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    @TypeConverter
    fun stringToOffsetDateTime(odt: String?): OffsetDateTime? =
        odt?.let { OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME) }

    @TypeConverter
    fun localDateToString(ld: LocalDate?): String? =
        ld?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
