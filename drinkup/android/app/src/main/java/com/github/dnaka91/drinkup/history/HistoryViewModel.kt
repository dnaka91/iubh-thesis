package com.github.dnaka91.drinkup.history

import androidx.lifecycle.ViewModel
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.core.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val drinkup: DrinkupService
) : ViewModel() {
    suspend fun history(): List<Record> = drinkup.history(LocalDate.now())
        .sortedByDescending { it.timestamp }
}
