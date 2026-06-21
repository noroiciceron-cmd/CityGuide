package com.example.cityguide

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class SavedPlace(
    val placeId: String,
    val placeName: String,
    val isFavorite: Boolean
)

data class PlaceNote(
    val id: Long,
    val placeId: String,
    val text: String,
    val updatedAt: Long
)

data class PlacePhoto(
    val id: Long,
    val placeId: String,
    val photoUri: String,
    val createdAt: Long
)
class SavedPlaceDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_SAVED_PLACES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                place_id TEXT UNIQUE NOT NULL,
                place_name TEXT NOT NULL,
                is_favorite INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        createNotesTable(db)
        createPhotosTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            createNotesTable(db)
            copyLegacyNotes(db)
        }
        if (oldVersion < 3) {
            createPhotosTable(db)
        }
    }

    fun getSavedPlace(place: Place): SavedPlace {
        readableDatabase.query(
            TABLE_SAVED_PLACES,
            arrayOf("place_id", "place_name", "is_favorite"),
            "place_id = ?",
            arrayOf(place.id),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return SavedPlace(
                    placeId = cursor.getString(cursor.getColumnIndexOrThrow("place_id")),
                    placeName = cursor.getString(cursor.getColumnIndexOrThrow("place_name")),
                    isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1
                )
            }
        }
        return SavedPlace(place.id, place.title, false)
    }

    fun setFavorite(place: Place, isFavorite: Boolean) {
        val values = ContentValues().apply {
            put("place_id", place.id)
            put("place_name", place.title)
            put("is_favorite", if (isFavorite) 1 else 0)
            put("updated_at", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict(
            TABLE_SAVED_PLACES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getFavoritePlaces(): List<SavedPlace> {
        val result = mutableListOf<SavedPlace>()
        readableDatabase.query(
            TABLE_SAVED_PLACES,
            arrayOf("place_id", "place_name", "is_favorite"),
            "is_favorite = 1",
            null,
            null,
            null,
            "updated_at DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    SavedPlace(
                        placeId = cursor.getString(cursor.getColumnIndexOrThrow("place_id")),
                        placeName = cursor.getString(cursor.getColumnIndexOrThrow("place_name")),
                        isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1
                    )
                )
            }
        }
        return result
    }

    fun getNotes(place: Place): List<PlaceNote> {
        val result = mutableListOf<PlaceNote>()
        readableDatabase.query(
            TABLE_NOTES,
            arrayOf("id", "place_id", "note_text", "updated_at"),
            "place_id = ?",
            arrayOf(place.id),
            null,
            null,
            "updated_at DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    PlaceNote(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        placeId = cursor.getString(cursor.getColumnIndexOrThrow("place_id")),
                        text = cursor.getString(cursor.getColumnIndexOrThrow("note_text")),
                        updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
                    )
                )
            }
        }
        return result
    }

    fun createNote(place: Place, text: String): Long {
        val values = ContentValues().apply {
            put("place_id", place.id)
            put("note_text", text)
            put("updated_at", System.currentTimeMillis())
        }
        return writableDatabase.insert(TABLE_NOTES, null, values)
    }

    fun updateNote(noteId: Long, text: String) {
        val values = ContentValues().apply {
            put("note_text", text)
            put("updated_at", System.currentTimeMillis())
        }
        writableDatabase.update(TABLE_NOTES, values, "id = ?", arrayOf(noteId.toString()))
    }

    fun deleteNote(noteId: Long) {
        writableDatabase.delete(TABLE_NOTES, "id = ?", arrayOf(noteId.toString()))
    }


    fun addPhoto(place: Place, photoUri: String): Long {
        val values = ContentValues().apply {
            put("place_id", place.id)
            put("photo_uri", photoUri)
            put("created_at", System.currentTimeMillis())
        }

        return writableDatabase.insert(
            TABLE_PHOTOS,
            null,
            values
        )
    }

    fun getPhotos(place: Place): List<PlacePhoto> {
        val result = mutableListOf<PlacePhoto>()

        readableDatabase.query(
            TABLE_PHOTOS,
            arrayOf("id", "place_id", "photo_uri", "created_at"),
            "place_id = ?",
            arrayOf(place.id),
            null,
            null,
            "created_at ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    PlacePhoto(
                        id = cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                        ),
                        placeId = cursor.getString(
                            cursor.getColumnIndexOrThrow("place_id")
                        ),
                        photoUri = cursor.getString(
                            cursor.getColumnIndexOrThrow("photo_uri")
                        ),
                        createdAt = cursor.getLong(
                            cursor.getColumnIndexOrThrow("created_at")
                        )
                    )
                )
            }
        }

        return result
    }

    private fun createNotesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_NOTES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                place_id TEXT NOT NULL,
                note_text TEXT NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }


    private fun createPhotosTable(db: SQLiteDatabase) {
        db.execSQL(
            """
        CREATE TABLE IF NOT EXISTS $TABLE_PHOTOS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            place_id TEXT NOT NULL,
            photo_uri TEXT NOT NULL,
            created_at INTEGER NOT NULL
        )
        """.trimIndent()
        )
    }

    private fun copyLegacyNotes(db: SQLiteDatabase) {
        runCatching {
            db.rawQuery(
                "SELECT place_id, note, updated_at FROM $TABLE_SAVED_PLACES WHERE note != ''",
                null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val values = ContentValues().apply {
                        put("place_id", cursor.getString(cursor.getColumnIndexOrThrow("place_id")))
                        put("note_text", cursor.getString(cursor.getColumnIndexOrThrow("note")))
                        put("updated_at", cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")))
                    }
                    db.insert(TABLE_NOTES, null, values)
                }
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "city_guide.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_SAVED_PLACES = "saved_places"
        private const val TABLE_NOTES = "place_notes"

        private const val TABLE_PHOTOS = "place_photos"
    }
}
