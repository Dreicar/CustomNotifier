package com.example.notifier

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editPattern: EditText
    private lateinit var editRepeat: EditText
    private lateinit var editApps: EditText
    private lateinit var btnSave: Button
    private lateinit var btnTest: Button
    private val prefs by lazy { getSharedPreferences("vib_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editPattern = findViewById(R.id.editPattern)
        editRepeat = findViewById(R.id.editRepeat)
        editApps = findViewById(R.id.editApps)
        btnSave = findViewById(R.id.btnSave)
        btnTest = findViewById(R.id.btnTest)

        // Загружаем текущие значения
        editPattern.setText(prefs.getString("vib_pattern", "0,400,100,400,100,400"))
        editRepeat.setText(prefs.getInt("vib_repeat", 2).toString())
        editApps.setText(prefs.getStringSet("allowed_apps", setOf())?.joinToString(",") ?: "")

        btnSave.setOnClickListener {
            val pattern = editPattern.text.toString()
            val repeat = editRepeat.text.toString().toIntOrNull() ?: 2
            val apps = editApps.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

            prefs.edit().apply {
                putString("vib_pattern", pattern)
                putInt("vib_repeat", repeat)
                putStringSet("allowed_apps", apps)
                apply()
            }
        }

        btnTest.setOnClickListener {
            testVibration()
        }
    }

    private fun testVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val patternStr = editPattern.text.toString()
        val repeat = editRepeat.text.toString().toIntOrNull() ?: 2

        val basePattern = patternStr.split(",").mapNotNull { it.trim().toLongOrNull() }.toLongArray()
        if (basePattern.isEmpty()) return

        // Повторяем паттерн
        val fullPattern = LongArray(basePattern.size * repeat) { i ->
            basePattern[i % basePattern.size]
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(fullPattern, -1))
        } else {
            vibrator.vibrate(fullPattern, -1)
        }
    }
}