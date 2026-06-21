package com.example.cityguide

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CloudActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var textTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleTextView = findViewById(R.id.cloudTitleTextView)
        textTextView = findViewById(R.id.cloudTextView)
        findViewById<Button>(R.id.reloadCloudButton).setOnClickListener { loadRecommendation() }

        loadRecommendation()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadRecommendation() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            showFallback("Firebase не настроен. Добавьте google-services.json и данные по пути /recommendation.")
            return
        }

        try {
            FirebaseDatabase.getInstance(DATABASE_URL).reference.child("recommendation")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val title = snapshot.child("title").getValue(String::class.java)
                        val text = snapshot.child("text").getValue(String::class.java)
                        if (title.isNullOrBlank() || text.isNullOrBlank()) {
                            showFallback("В Firebase нет рекомендации по пути /recommendation.")
                        } else {
                            titleTextView.text = title
                            textTextView.text = text
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showFallback("Не удалось загрузить рекомендацию: ${error.message}")
                    }
                })
        } catch (error: IllegalStateException) {
            showFallback("Firebase не настроен. Добавьте google-services.json и повторите запуск.")
        }
    }

    private fun showFallback(message: String) {
        titleTextView.text = "Облачная рекомендация"
        textTextView.text = message
    }

    companion object {
        private const val DATABASE_URL = "https://cityguide-9xxx2-default-rtdb.firebaseio.com"
    }
}
