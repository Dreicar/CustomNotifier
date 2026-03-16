package com.example.notifier

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class MyNotificationListener : NotificationListenerService() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("vib_prefs", Context.MODE_PRIVATE)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val allowedApps = prefs.getStringSet("allowed_apps", setOf()) ?: setOf()

        if (allowedApps.isEmpty() || allowedApps.contains(sbn?.packageName)) {
            vibrateCustom()
        }
    }

    private fun vibrateCustom() {

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val patternStr =
            prefs.getString("vib_pattern", "0,400,100,400,100,400")
                ?: "0,400,100,400,100,400"

        val repeatCount = prefs.getInt("vib_repeat", 2)

        val basePattern =
            patternStr.split(",").map { it.trim().toLong() }.toLongArray()

        val fullPattern =
            LongArray(basePattern.size * repeatCount) { i ->
                basePattern[i % basePattern.size]
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(fullPattern, -1))
        } else {
            vibrator.vibrate(fullPattern, -1)
        }
    }
}