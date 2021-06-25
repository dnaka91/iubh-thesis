package com.github.dnaka91.drinkup.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.github.dnaka91.drinkup.MainActivity
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.core.DrinkupService
import com.github.dnaka91.drinkup.extensions.scheduleNextAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    internal lateinit var drinkup: DrinkupService

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        val powerManager = context.getSystemService<PowerManager>() ?: return

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannelCompat.Builder(
                    CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_HIGH
                )
                    .setName(context.getString(R.string.drink_alarm_channel_name))
                    .build()
            )
        }

        val amount = runBlocking { drinkup.drinkAmount() }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_round_local_drink_24)
            .setContentTitle(context.getString(R.string.drink_alarm_title))
            .setContentText(context.getString(R.string.drink_alarm_message, amount))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        if (powerManager.isInteractive) {
            val contentIntent = createContentIntent(context)
            notificationManager.notify(1, notification.setContentIntent(contentIntent).build())
        } else {
            val fullscreenIntent = createFullScreenIntent(context)
            notificationManager.notify(
                1,
                notification.setFullScreenIntent(fullscreenIntent, true).build()
            )
        }

        runBlocking {
            context.scheduleNextAlarm(drinkup)
        }
    }

    companion object {
        private const val CHANNEL_ID = "drink_alarms"

        private fun createContentIntent(context: Context): PendingIntent {
            return MainActivity.newInstance(context).let {
                PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        private fun createFullScreenIntent(context: Context): PendingIntent {
            return DrinkAlarmActivity.newInstance(context)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .let {
                    PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
                }
        }

    }
}
