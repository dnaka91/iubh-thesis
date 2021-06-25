package com.github.dnaka91.drinkup.sizes

import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.IntakeSize
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntakeSizeViewModel @Inject constructor(
    private val drinkup: DrinkupService
) : ViewModel() {
    suspend fun intakeSizes(): List<IntakeSize> = drinkup.listIntakeSizes()
}
