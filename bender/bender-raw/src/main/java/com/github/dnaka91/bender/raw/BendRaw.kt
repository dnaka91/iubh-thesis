package com.github.dnaka91.bender.raw

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native

object BendRaw {
    @Suppress("FunctionName")
    internal interface BendLibrary : Library {
        companion object {
            var INSTANCE = Native.load("bend_raw", BendLibrary::class.java) as BendLibrary

            init {
                INSTANCE.bend_init()
            }
        }

        fun bend_init()

        fun bend_empty()

        fun bend_hello(name: String): String

        fun bend_mandelbrot(width: Int, height: Int, result: ByteArray)
        fun bend_mandelbrot_manual(width: Int, height: Int, result: ByteArray)

        fun load_person(id: Long): Long
        fun load_person_json(id: Long): String
        fun load_person_direct(id: Long): CPerson

        fun person_free(id: Long)

        fun person_get_id(ptr: Long): Long
        fun person_get_name(ptr: Long): Long
        fun person_get_gender(ptr: Long): Int
        fun person_get_weight(ptr: Long): Double
        fun person_get_birthday_year(ptr: Long): Int
        fun person_get_birthday_month(ptr: Long): Int
        fun person_get_birthday_day(ptr: Long): Int
        fun person_get_address(ptr: Long, index: Long): Long
        fun person_get_address_len(ptr: Long): Long
        fun person_get_total_steps(ptr: Long): Long

        fun name_get_title(ptr: Long): String?
        fun name_get_first(ptr: Long): String
        fun name_get_middle(ptr: Long): String?
        fun name_get_last(ptr: Long): String

        fun address_get_street(ptr: Long): String
        fun address_get_house_number(ptr: Long): String
        fun address_get_city(ptr: Long): String
        fun address_get_country(ptr: Long): String
        fun address_get_postal_code(ptr: Long): String
        fun address_get_detail(ptr: Long, index: Long): String
        fun address_get_detail_len(ptr: Long): Long

        fun callback(): CCallback
    }

    fun empty() = BendLibrary.INSTANCE.bend_empty()

    fun hello(name: String): String = BendLibrary.INSTANCE.bend_hello(name)

    fun mandelbrot(width: Int, height: Int): ByteArray = ByteArray(width * height).also {
        BendLibrary.INSTANCE.bend_mandelbrot(width, height, it)
    }

    fun mandelbrotManual(width: Int, height: Int): ByteArray = ByteArray(width * height).also {
        BendLibrary.INSTANCE.bend_mandelbrot_manual(width, height, it)
    }

    fun loadPerson(id: Long): Person =
        BendLibrary.INSTANCE.load_person(id).let(Person.Companion::fromNative)

    fun loadPersonJson(id: Long): Person =
        BendLibrary.INSTANCE.load_person_json(id).let { JsonAdapter.person.fromJson(it)!! }

    fun loadPersonDirect(id: Long): CPerson = BendLibrary.INSTANCE.load_person_direct(id)

    fun callback(): CCallback = BendLibrary.INSTANCE.callback()

    interface CCallback : Callback {
        fun invoke(result: Int)
    }
}