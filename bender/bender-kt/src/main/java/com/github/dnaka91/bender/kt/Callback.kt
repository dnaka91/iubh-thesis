package com.github.dnaka91.bender.kt

object Callback {
    fun interface CompletionListener {
        fun onComplete(result: Int)
    }

    fun callback(listener: CompletionListener) {
        listener.onComplete(5)
    }
}