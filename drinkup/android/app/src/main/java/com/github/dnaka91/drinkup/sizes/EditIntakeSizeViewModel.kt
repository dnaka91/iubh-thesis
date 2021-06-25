package com.github.dnaka91.drinkup.sizes

import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.IntakeSize
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditIntakeSizeViewModel @Inject constructor(
    private val drinkup: DrinkupService
) : ViewModel() {
    var id = 0
    private var name = ""
    private var amount = 0

    fun onNameChanged(value: String) {
        name = value
    }

    fun onAmountChanged(value: Int) {
        amount = value
    }

    suspend fun save(): Boolean {
        drinkup.saveIntakeSize(IntakeSize(id, name, amount))
        return true
    }
}
