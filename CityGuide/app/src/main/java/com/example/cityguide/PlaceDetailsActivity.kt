package com.example.cityguide

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlaceDetailsActivity : AppCompatActivity() {
    private lateinit var place: Place
    private lateinit var dbHelper: SavedPlaceDbHelper
    private lateinit var noteEditText: EditText
    private lateinit var favoriteButton: Button
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var emptyNotesTextView: TextView
    private var mediaPlayer: MediaPlayer? = null
    private var selectedNoteId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_details)

        val placeId = intent.getStringExtra(EXTRA_PLACE_ID) ?: PlacesRepository.places.first().id
        place = PlacesRepository.findById(placeId)
        dbHelper = SavedPlaceDbHelper(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = place.title
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.placeTitleTextView).text = place.title
        findViewById<TextView>(R.id.placeCategoryTextView).text = place.category
        findViewById<TextView>(R.id.placeDescriptionTextView).text = place.description
        findViewById<ImageView>(R.id.placeImageView).setImageResource(place.imageResId)
        noteEditText = findViewById(R.id.noteEditText)
        favoriteButton = findViewById(R.id.favoriteButton)
        emptyNotesTextView = findViewById(R.id.emptyNotesTextView)
        noteAdapter = NoteAdapter(emptyList()) { note -> selectNote(note) }
        findViewById<RecyclerView>(R.id.notesRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@PlaceDetailsActivity)
            adapter = noteAdapter
            isNestedScrollingEnabled = false
        }

        loadSavedState()

        findViewById<Button>(R.id.createNoteButton).setOnClickListener { createNote() }
        findViewById<Button>(R.id.updateNoteButton).setOnClickListener { updateNote() }
        findViewById<Button>(R.id.deleteNoteButton).setOnClickListener { deleteNote() }
        favoriteButton.setOnClickListener { toggleFavorite() }
        findViewById<Button>(R.id.audioButton).setOnClickListener { playAudioGuide() }
        findViewById<Button>(R.id.videoButton).setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }
        findViewById<Button>(R.id.cameraButton).setOnClickListener { openCamera() }
        findViewById<Button>(R.id.notifyButton).setOnClickListener { showPlaceNotification() }
        findViewById<Button>(R.id.mapButton).setOnClickListener {
            startActivity(
                Intent(this, MapActivity::class.java)
                    .putExtra(MapActivity.EXTRA_PLACE_ID, place.id)
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @Deprecated("Used to match the lab camera intent example.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                findViewById<ImageView>(R.id.cameraImageView).setImageBitmap(bitmap)
            } else {
                showMessage("Камера не вернула изображение.")
            }
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun loadSavedState() {
        val saved = dbHelper.getSavedPlace(place)
        updateFavoriteButton(saved.isFavorite)
        reloadNotes()
    }

    private fun createNote() {
        val text = noteEditText.text.toString().trim()
        if (text.isBlank()) {
            showMessage("Введите текст заметки.")
            return
        }
        dbHelper.createNote(place, text)
        clearNoteSelection()
        reloadNotes()
        showMessage("Заметка создана.")
    }

    private fun updateNote() {
        val noteId = selectedNoteId
        if (noteId == null) {
            showMessage("Выберите заметку из списка.")
            return
        }
        val text = noteEditText.text.toString().trim()
        if (text.isBlank()) {
            showMessage("Введите текст заметки.")
            return
        }
        dbHelper.updateNote(noteId, text)
        clearNoteSelection()
        reloadNotes()
        showMessage("Заметка изменена.")
    }

    private fun deleteNote() {
        val noteId = selectedNoteId
        if (noteId == null) {
            showMessage("Выберите заметку из списка.")
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Удалить заметку?")
            .setMessage("Заметка будет удалена из базы данных.")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                dbHelper.deleteNote(noteId)
                clearNoteSelection()
                reloadNotes()
                showMessage("Заметка удалена.")
            }
            .show()
    }

    private fun selectNote(note: PlaceNote) {
        selectedNoteId = note.id
        noteEditText.setText(note.text)
        showMessage("Заметка выбрана для изменения.")
    }

    private fun clearNoteSelection() {
        selectedNoteId = null
        noteEditText.setText("")
    }

    private fun reloadNotes() {
        val notes = dbHelper.getNotes(place)
        noteAdapter.update(notes)
        emptyNotesTextView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun toggleFavorite() {
        val current = dbHelper.getSavedPlace(place)
        val newValue = !current.isFavorite
        dbHelper.setFavorite(place, newValue)
        favoriteButton.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))
        updateFavoriteButton(newValue)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        favoriteButton.text = if (isFavorite) "Убрать из избранного" else "Добавить в избранное"
    }

    private fun playAudioGuide() {
        val audioResId = resources.getIdentifier("audio_guide", "raw", packageName)
        if (audioResId == 0) {
            AlertDialog.Builder(this)
                .setTitle("Аудиофайл не найден")
                .setMessage("Добавьте файл app/src/main/res/raw/audio_guide.mp3 и запустите приложение заново.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, audioResId)
        mediaPlayer?.start()
        showMessage("Воспроизводится аудиогид.")
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent, REQUEST_CAMERA)
        } catch (error: Exception) {
            showMessage("Камера недоступна.")
        }
    }

    private fun showPlaceNotification() {
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATIONS
            )
            return
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Гид по Хабаровску")
            .setContentText("Не забудьте посетить: ${place.title}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(place.id.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Рекомендации CityGuide",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_PLACE_ID = "extra_place_id"
        private const val REQUEST_CAMERA = 10
        private const val REQUEST_NOTIFICATIONS = 11
        private const val CHANNEL_ID = "city_guide_places"
    }
}
