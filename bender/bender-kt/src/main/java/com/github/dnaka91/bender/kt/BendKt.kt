package com.github.dnaka91.bender.kt

object BendKt {
    fun empty() = Empty.empty()

    fun hello(name: String) = Hello.hello(name)

    fun mandelbrot(width: Int, height: Int) = Mandelbrot.render(width, height)

    fun mandelbrotManual(width: Int, height: Int) = MandelbrotManual.render(width, height)

    fun loadPerson(id: Long) = PersonImpl.loadPerson(id)

    fun loadPersonJson(id: Long) = PersonImpl.loadPersonJson(id)

    fun savePerson(person: Person) = PersonImpl.savePerson(person)

    fun savePersonJson(person: Person) = PersonImpl.savePersonJson(person)

    fun callback(listener: Callback.CompletionListener) = Callback.callback(listener)
}