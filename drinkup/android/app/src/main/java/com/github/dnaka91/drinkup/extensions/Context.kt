package com.github.dnaka91.drinkup.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.getSystemService
import com.github.dnaka91.drinkup.alarm.AlarmReceiver
import com.github.dnaka91.drinkup.core.DrinkupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

suspend fun Context.scheduleNextAlarm(drinkup: DrinkupService) {
    val alarmIntent = Intent(this, AlarmReceiver::class.java).let {
        PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val nextAlarm = drinkup.nextAlarm()
    if (nextAlarm == null) {
        getSystemService<AlarmManager>()?.cancel(alarmIntent)
        return
    }

    val alarmInfo = AlarmManager.AlarmClockInfo(
        OffsetDateTime.now().plusSeconds(nextAlarm.seconds).toEpochSecond() * 1000,
        null
    )

    getSystemService<AlarmManager>()?.setAlarmClock(alarmInfo, alarmIntent)

    withContext(Dispatchers.Main) {
        Toast.makeText(
            this@scheduleNextAlarm,
            "Next alarm in ${nextAlarm.toMinutes()}min",
            Toast.LENGTH_LONG
        ).show()
    }
}
