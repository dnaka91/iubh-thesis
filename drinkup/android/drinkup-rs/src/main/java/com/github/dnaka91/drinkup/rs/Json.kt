package com.github.dnaka91.drinkup.rs

import com.squareup.moshi.*
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal object Json {
    private val moshi = Moshi.Builder()
        .add(OffsetDateTimeAdapter())
        .add(LocalTimeAdapter())
        .build()

    internal val schedule: JsonAdapter<Schedule> = moshi.adapter(Schedule::class.java)

    internal val intakeSize: JsonAdapter<IntakeSize> = moshi.adapter(IntakeSize::class.java)

    internal val intakeSizeList: JsonAdapter<List<IntakeSize>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, IntakeSize::class.java))

    internal val record: JsonAdapter<Record> = moshi.adapter(Record::class.java)

    internal val recordList: JsonAdapter<List<Record>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, Record::class.java))

    class OffsetDateTimeAdapter {
        @ToJson
        fun toJson(odt: OffsetDateTime): String =
            odt.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        @FromJson
        fun fromJson(odt: String): OffsetDateTime =
            OffsetDateTime.parse(odt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    class LocalTimeAdapter {
        @ToJson
        fun toJson(lt: LocalTime): String = lt.format(DateTimeFormatter.ISO_LOCAL_TIME)

        @FromJson
        fun fromJson(lt: String): LocalTime = LocalTime.parse(lt, DateTimeFormatter.ISO_LOCAL_TIME)
    }
}
