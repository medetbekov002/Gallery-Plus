package com.example.task.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PhotoDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "photos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "photos_library"

        private const val COLUMN_ID = "id"
        private const val COLUMN_IMAGE_PATH = "image_path"
        private const val COLUMN_CREATION_DATE = "creation_date"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $COLUMN_IMAGE_PATH TEXT," +
                    " $COLUMN_CREATION_DATE INTEGER)"
        p0?.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(p0)
    }

    fun insertPhoto(path: String, creationDate: Long) {
        val values = ContentValues().apply {
//            put(COLUMN_ID, photo.id)
            put(COLUMN_IMAGE_PATH, path)
            put(COLUMN_CREATION_DATE, creationDate)
        }

        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
    }

    fun removePhoto(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val query = "SELECT * FROM $TABLE_NAME"

        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH))
                val creationDate = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATION_DATE))

                val photo = Photo(id, imagePath, creationDate)
                photos.add(photo)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return photos
    }

    @SuppressLint("Range")
    fun getInvalidPhotos(timeStamp: Long): List<Photo> {
        val photos = ArrayList<Photo>()
        val db = readableDatabase

        val selection = "$COLUMN_CREATION_DATE < ?"
        val selectionArgs = arrayOf(timeStamp.toString())

        val cursor: Cursor? = db.query(
            TABLE_NAME, null, selection, selectionArgs, null, null, null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH))
                val creationDate = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATION_DATE))

                val photo = Photo(id, imagePath, creationDate)
                photos.add(photo)
            }
        }

        return photos
    }


}