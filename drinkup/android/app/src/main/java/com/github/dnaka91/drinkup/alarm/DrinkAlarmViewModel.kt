package com.github.dnaka91.drinkup.alarm

import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrinkAlarmViewModel @Inject constructor(private val drinkup: DrinkupService) : ViewModel() {
    private var vibrated = false

    fun vibrate(): Boolean {
        val v = vibrated
        vibrated = true
        return v
    }

    suspend fun drinkAmount(): Int = drinkup.drinkAmount()
}
