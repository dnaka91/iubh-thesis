package com.github.dnaka91.drinkup.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.extensions.scheduleNextAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SchedulingBootReceiver : BroadcastReceiver() {
    @Inject
    internal lateinit var drinkup: DrinkupService

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            runBlocking { context.scheduleNextAlarm(drinkup) }
        }
    }
}
