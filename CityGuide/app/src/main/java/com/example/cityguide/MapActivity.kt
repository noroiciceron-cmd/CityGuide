package com.example.cityguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.applyStatusBarInsets()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val selectedPlaceId = intent.getStringExtra(EXTRA_PLACE_ID)
        val selectedPlace = selectedPlaceId?.let { PlacesRepository.findById(it) }
        val center = selectedPlace ?: PlacesRepository.places.first()
        mapView.controller.setZoom(if (selectedPlace == null) 12.5 else 15.0)
        mapView.controller.setCenter(GeoPoint(center.latitude, center.longitude))

        PlacesRepository.places.forEach { place ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(place.latitude, place.longitude)
            marker.title = place.title
            marker.subDescription = place.category
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_PLACE_ID = "extra_place_id"
    }
}
