package com.github.dnaka91.bender.kt

import java.time.LocalDate

object PersonImpl {
    fun loadPerson(id: Long): Person = Person(
        id = id,
        name = Name(
            title = "Doctor",
            first = "Max",
            middle = null,
            last = "Mustermann"
        ),
        gender = Gender.Male,
        birthday = LocalDate.of(1990, 3, 14),
        addresses = listOf(
            Address(
                street = "Sample Street 1-2",
                houseNumber = "33b",
                city = "Sampleton",
                country = "Samplevania",
                postalCode = "111-222",
                details = listOf("Room 5")
            )
        ),
        weight = 72.5,
        totalSteps = 33095,
    )

    fun savePerson(person: Person) {
        println(person.toString())
    }

    fun loadPersonJson(id: Long): Person = JsonAdapter.person.toJson(loadPerson(id)).let {
        JsonAdapter.person.fromJson(it)!!
    }

    fun savePersonJson(person: Person) {
        JsonAdapter.person.toJson(person).let {
            val value = JsonAdapter.person.fromJson(it)!!
            println(value.toString())
        }
    }
}