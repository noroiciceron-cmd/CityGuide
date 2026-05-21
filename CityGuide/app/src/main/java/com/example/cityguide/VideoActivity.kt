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

        val videoResId = resources.getIdentifier("city_video", "raw", packageName)
        if (videoResId == 0) {
            AlertDialog.Builder(this)
                .setTitle("Видеофайл не найден")
                .setMessage("Добавьте файл app/src/main/res/raw/city_video.mp4 и запустите приложение заново.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
            return
        }

        val videoView: VideoView = findViewById(R.id.videoView)
        videoView.setVideoURI(Uri.parse("android.resource://$packageName/$videoResId"))
        videoView.setMediaController(MediaController(this))
        videoView.requestFocus()
        videoView.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
