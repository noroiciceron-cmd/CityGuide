package com.example.cityguide

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    private lateinit var notificationsCheckBox: CheckBox
    private lateinit var showVisitedCheckBox: CheckBox
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        notificationsCheckBox = findViewById(R.id.notificationsCheckBox)
        showVisitedCheckBox = findViewById(R.id.showVisitedCheckBox)
        statusTextView = findViewById(R.id.settingsStatusTextView)

        loadSettings()
        findViewById<Button>(R.id.saveSettingsButton).setOnClickListener { saveSettings() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        notificationsCheckBox.isChecked = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        showVisitedCheckBox.isChecked = prefs.getBoolean(KEY_SHOW_VISITED, true)
        updateStatus()
    }

    private fun saveSettings() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NOTIFICATIONS, notificationsCheckBox.isChecked)
            .putBoolean(KEY_SHOW_VISITED, showVisitedCheckBox.isChecked)
            .apply()
        updateStatus()
        Toast.makeText(this, "Настройки сохранены.", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus() {
        statusTextView.text = "Напоминания: ${if (notificationsCheckBox.isChecked) "включены" else "выключены"}\n" +
            "Посещённые места: ${if (showVisitedCheckBox.isChecked) "показываются" else "скрыты"}"
    }

    companion object {
        private const val PREFS_NAME = "city_guide_settings"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val KEY_SHOW_VISITED = "show_visited"
    }
}
