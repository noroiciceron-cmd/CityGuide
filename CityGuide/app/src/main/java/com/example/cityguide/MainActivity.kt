package com.example.cityguide

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: PlaceAdapter
    private lateinit var resultTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var dbHelper: SavedPlaceDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = SavedPlaceDbHelper(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)

        resultTextView = findViewById(R.id.resultTextView)
        searchEditText = findViewById(R.id.searchEditText)
        val recyclerView: RecyclerView = findViewById(R.id.placesRecyclerView)

        adapter = PlaceAdapter(
            PlacesRepository.places,
            onClick = { openDetails(it) },
            onLongClick = { view, place -> showPlaceContextMenu(view, place) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.searchButton).setOnClickListener { filterPlaces() }
        findViewById<Button>(R.id.mapButton).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_saved -> {
                startActivity(Intent(this, SavedPlacesActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_cloud -> {
                startActivity(Intent(this, CloudActivity::class.java))
                true
            }
            R.id.menu_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterPlaces() {
        val query = searchEditText.text.toString().trim()
        val filtered = if (query.isBlank()) {
            PlacesRepository.places
        } else {
            PlacesRepository.places.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
            }
        }

        adapter.update(filtered)
        resultTextView.text = if (filtered.isEmpty()) {
            "По запросу \"$query\" ничего не найдено"
        } else {
            "Найдено мест: ${filtered.size}"
        }
    }

    private fun openDetails(place: Place) {
        startActivity(
            Intent(this, PlaceDetailsActivity::class.java)
                .putExtra(PlaceDetailsActivity.EXTRA_PLACE_ID, place.id)
        )
    }

    private fun showPlaceContextMenu(anchor: View, place: Place) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.place_context_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.context_favorite -> {
                    dbHelper.setFavorite(place, true)
                    Toast.makeText(this, "Добавлено в избранное: ${place.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("О программе")
            .setMessage(getString(R.string.about_text))
            .setPositiveButton("OK", null)
            .show()
    }
}
