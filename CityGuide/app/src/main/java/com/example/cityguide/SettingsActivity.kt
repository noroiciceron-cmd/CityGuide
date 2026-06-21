package com.example.cityguide

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var videoAutoplayCheckBox: CheckBox
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        videoAutoplayCheckBox = findViewById(R.id.videoAutoplayCheckBox)
        statusTextView = findViewById(R.id.settingsStatusTextView)

        loadSettings()

        findViewById<Button>(R.id.saveSettingsButton).setOnClickListener {
            saveSettings()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        videoAutoplayCheckBox.isChecked = prefs.getBoolean(
            KEY_VIDEO_AUTOPLAY,
            true
        )

        updateStatus()
    }

    private fun saveSettings() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(
                KEY_VIDEO_AUTOPLAY,
                videoAutoplayCheckBox.isChecked
            )
            .remove(KEY_OLD_NOTIFICATIONS)
            .remove(KEY_OLD_SHOW_VISITED)
            .apply()

        updateStatus()

        Toast.makeText(
            this,
            getString(R.string.settings_saved),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateStatus() {
        val status = if (videoAutoplayCheckBox.isChecked) {
            getString(R.string.video_autoplay_enabled)
        } else {
            getString(R.string.video_autoplay_disabled)
        }

        statusTextView.text = getString(
            R.string.video_autoplay_status,
            status
        )
    }

    companion object {
        const val PREFS_NAME = "city_guide_settings"
        const val KEY_VIDEO_AUTOPLAY = "video_autoplay"

        private const val KEY_OLD_NOTIFICATIONS = "notifications"
        private const val KEY_OLD_SHOW_VISITED = "show_visited"
    }
}