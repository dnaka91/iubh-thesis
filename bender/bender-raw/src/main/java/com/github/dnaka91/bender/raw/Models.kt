package com.github.dnaka91.bender.raw

import com.squareup.moshi.Json
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
    @Json(name = "total_steps")
    val totalSteps: Long
) {
    @Transient
    private var ptr: Long? = null

    protected fun finalize() {
        ptr?.let {
            BendRaw.BendLibrary.INSTANCE.person_free(it)
        }
    }

    companion object {
        internal fun fromNative(ptr: Long) = Person(
            BendRaw.BendLibrary.INSTANCE.person_get_id(ptr),
            BendRaw.BendLibrary.INSTANCE.person_get_name(ptr).let(Name.Companion::fromNative),
            BendRaw.BendLibrary.INSTANCE.person_get_gender(ptr)
                .let(Gender.Companion::fromNative),
            LocalDate.of(
                BendRaw.BendLibrary.INSTANCE.person_get_birthday_year(ptr),
                BendRaw.BendLibrary.INSTANCE.person_get_birthday_month(ptr),
                BendRaw.BendLibrary.INSTANCE.person_get_birthday_day(ptr)
            ),
            BendRaw.BendLibrary.INSTANCE.person_get_address_len(ptr).let { len ->
                (0 until len).map {
                    BendRaw.BendLibrary.INSTANCE.person_get_address(ptr, it)
                        .let(Address.Companion::fromNative)
                }
            },
            BendRaw.BendLibrary.INSTANCE.person_get_weight(ptr),
            BendRaw.BendLibrary.INSTANCE.person_get_total_steps(ptr)
        ).also { it.ptr = ptr }
    }
}

sealed class Gender {
    object Female : Gender()
    object Male : Gender()
    data class Other(val value: String) : Gender()

    companion object {
        internal fun fromNative(value: Int) = when (value) {
            0 -> Female
            1 -> Male
            2 -> Other("")
            else -> throw Exception("Unknown gender '$value'")
        }
    }

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
    val last: String
) {
    companion object {
        internal fun fromNative(ptr: Long) = Name(
            BendRaw.BendLibrary.INSTANCE.name_get_title(ptr),
            BendRaw.BendLibrary.INSTANCE.name_get_first(ptr),
            BendRaw.BendLibrary.INSTANCE.name_get_middle(ptr),
            BendRaw.BendLibrary.INSTANCE.name_get_last(ptr)
        )
    }
}

@JsonClass(generateAdapter = true)
data class Address(
    val street: String,
    @Json(name = "house_number")
    val houseNumber: String,
    val city: String,
    val country: String,
    @Json(name = "postal_code")
    val postalCode: String,
    val details: List<String>
) {
    companion object {
        internal fun fromNative(ptr: Long) = Address(
            BendRaw.BendLibrary.INSTANCE.address_get_street(ptr),
            BendRaw.BendLibrary.INSTANCE.address_get_house_number(ptr),
            BendRaw.BendLibrary.INSTANCE.address_get_city(ptr),
            BendRaw.BendLibrary.INSTANCE.address_get_country(ptr),
            BendRaw.BendLibrary.INSTANCE.address_get_postal_code(ptr),
            BendRaw.BendLibrary.INSTANCE.address_get_detail_len(ptr).let { len ->
                (0 until len).map {
                    BendRaw.BendLibrary.INSTANCE.address_get_detail(ptr, it)
                }
            }
        )
    }
}
