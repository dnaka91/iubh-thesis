package com.github.dnaka91.drinkup

import android.content.Context
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.kt.AppDatabase
import com.github.dnaka91.drinkup.kt.Drinkup
import com.github.dnaka91.drinkup.kt.SchedulePreferences
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.create(context)

    @Singleton
    @Provides
    fun provideSchedulePreferences(): SchedulePreferences = SchedulePreferences()

    @Singleton
    @Provides
    fun provideDrinkUpInstance(
        db: AppDatabase,
        schedulePrefs: SchedulePreferences
    ): DrinkupService =
        Drinkup(db, schedulePrefs)
}
