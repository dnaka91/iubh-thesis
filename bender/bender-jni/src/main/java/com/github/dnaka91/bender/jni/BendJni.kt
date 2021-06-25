package com.github.dnaka91.bender.jni

object BendJni {
    init {
        System.loadLibrary("bend_jni")
        init()
    }

    private external fun init()

    external fun empty()

    external fun hello(name: String): String

    external fun mandelbrot(width: Int, height: Int): ByteArray

    external fun mandelbrotManual(width: Int, height: Int): ByteArray

    external fun loadPerson(id: Long): Person

    private external fun loadPersonJson(id: Long): String

    external fun savePerson(person: Person)

    private external fun savePersonJson(person: String)

    external fun callback(listener: CompletionListener)

    fun interface CompletionListener {
        fun onComplete(result: Int)
    }

    fun loadPersonJson2(id: Long): Person = loadPersonJson(id).let {
        JsonAdapter.person.fromJson(it)!!
    }

    fun savePersonJson2(person: Person) = JsonAdapter.person.toJson(person).let {
        savePersonJson(it)
    }
}