package com.example.cityguide

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CityPlaceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val placeOfDay = getPlaceOfDay(context)

        appWidgetIds.forEach { appWidgetId ->
            updateWidget(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
                place = placeOfDay
            )
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        place: Place
    ) {
        val intent = Intent(context, PlaceDetailsActivity::class.java).apply {
            putExtra(PlaceDetailsActivity.EXTRA_PLACE_ID, place.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            place.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val views = RemoteViews(
            context.packageName,
            R.layout.widget_city_place
        )

        views.setTextViewText(R.id.widgetPlaceTextView, place.title)
        views.setImageViewResource(R.id.widgetImageView, place.imageResId)
        views.setContentDescription(R.id.widgetImageView, place.title)

        views.setOnClickPendingIntent(R.id.widgetRootLayout, pendingIntent)
        views.setOnClickPendingIntent(R.id.widgetPlaceTextView, pendingIntent)
        views.setOnClickPendingIntent(R.id.widgetImageView, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPlaceOfDay(context: Context): Place {
        val today = SimpleDateFormat(
            DATE_PATTERN,
            Locale.getDefault()
        ).format(Date())

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val savedDate = prefs.getString(KEY_WIDGET_DATE, null)
        val savedPlaceId = prefs.getString(KEY_WIDGET_PLACE_ID, null)

        if (savedDate == today && savedPlaceId != null) {
            return PlacesRepository.findById(savedPlaceId)
        }

        val availablePlaces = if (savedPlaceId != null) {
            PlacesRepository.places.filter { place ->
                place.id != savedPlaceId
            }
        } else {
            PlacesRepository.places
        }

        val newPlace = availablePlaces.random()

        prefs.edit()
            .putString(KEY_WIDGET_DATE, today)
            .putString(KEY_WIDGET_PLACE_ID, newPlace.id)
            .apply()

        return newPlace
    }

    companion object {
        private const val PREFS_NAME = "city_widget_prefs"
        private const val KEY_WIDGET_DATE = "widget_date"
        private const val KEY_WIDGET_PLACE_ID = "widget_place_id"
        private const val DATE_PATTERN = "yyyy-MM-dd"
    }
}