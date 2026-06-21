package com.example.cityguide

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val videoResId = resources.getIdentifier(
            "city_video",
            "raw",
            packageName
        )

        if (videoResId == 0) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.video_file_not_found_title))
                .setMessage(getString(R.string.video_file_not_found_message))
                .setPositiveButton(getString(R.string.ok)) { _, _ -> finish() }
                .show()
            return
        }

        val videoView: VideoView = findViewById(R.id.videoView)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        val prefs = getSharedPreferences(
            SettingsActivity.PREFS_NAME,
            MODE_PRIVATE
        )

        val autoplayEnabled = prefs.getBoolean(
            SettingsActivity.KEY_VIDEO_AUTOPLAY,
            true
        )

        videoView.setOnPreparedListener {
            if (autoplayEnabled) {
                videoView.start()
            } else {
                videoView.pause()
                videoView.seekTo(1)
            }
        }

        videoView.setVideoURI(
            Uri.parse("android.resource://$packageName/$videoResId")
        )

        videoView.requestFocus()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}