package com.github.dnaka91.drinkup.kt

import splitties.preferences.Preferences
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SchedulePreferences : Preferences("schedule") {
    var start by stringPref("start", LocalTime.of(8, 0).format(DateTimeFormatter.ISO_LOCAL_TIME))
    var end by stringPref("end", LocalTime.of(22, 0).format(DateTimeFormatter.ISO_LOCAL_TIME))
    var goal by intPref("goal", 2400)
}
