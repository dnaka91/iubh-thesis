package com.github.dnaka91.drinkup

import android.content.Context
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.rs.Drinkup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DrinkupServiceFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun create(): DrinkupService {
        return Drinkup.Instance(context.filesDir.absolutePath)
    }
}
