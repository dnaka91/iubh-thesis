package com.github.dnaka91.drinkup

import android.content.Context
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.rs.Drinkup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DrinkupModule {
    @Singleton
    @Provides
    fun provideDrinkUpInstance(@ApplicationContext context: Context): DrinkupService =
        Drinkup.Instance(context.filesDir.absolutePath)
}
