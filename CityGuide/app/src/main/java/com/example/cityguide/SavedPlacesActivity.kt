package com.example.cityguide

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedPlacesActivity : AppCompatActivity() {
    private lateinit var dbHelper: SavedPlaceDbHelper
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: SavedPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_places)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = SavedPlaceDbHelper(this)
        emptyTextView = findViewById(R.id.emptyTextView)
        val recyclerView: RecyclerView = findViewById(R.id.savedRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SavedPlaceAdapter(emptyList()) { savedPlace ->
            startActivity(
                Intent(this, PlaceDetailsActivity::class.java)
                    .putExtra(PlaceDetailsActivity.EXTRA_PLACE_ID, savedPlace.placeId)
            )
        }
        recyclerView.adapter = adapter
        reloadFavorites()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            reloadFavorites()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun reloadFavorites() {
        val savedPlaces = dbHelper.getFavoritePlaces()
        emptyTextView.visibility = if (savedPlaces.isEmpty()) View.VISIBLE else View.GONE
        adapter.update(savedPlaces)
    }
}
