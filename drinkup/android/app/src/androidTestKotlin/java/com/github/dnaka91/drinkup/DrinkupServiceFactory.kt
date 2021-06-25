package com.github.dnaka91.drinkup

import android.content.Context
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.kt.AppDatabase
import com.github.dnaka91.drinkup.kt.Drinkup
import com.github.dnaka91.drinkup.kt.SchedulePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DrinkupServiceFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun create(): DrinkupService = Drinkup(AppDatabase.create(context), SchedulePreferences())
}
