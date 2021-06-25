package com.github.dnaka91.bender.app

interface Runner {
    val features: Set<Feature>

    fun empty()
    fun hello(name: String): String
    fun mandelbrot(width: Int, height: Int): ByteArray
    fun mandelbrotManual(width: Int, height: Int): ByteArray
    fun loadPerson(id: Long)
    fun savePerson()
    fun loadPersonJson(id: Long)
    fun savePersonJson()
    fun callback()
}
