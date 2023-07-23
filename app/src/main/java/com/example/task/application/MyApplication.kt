package com.example.task.application

import android.app.Application
import com.example.task.model.PhotoDbHelper

class MyApplication : Application() {
    companion object {
        lateinit var databaseHelper: PhotoDbHelper
    }

    override fun onCreate() {
        super.onCreate()
            databaseHelper = PhotoDbHelper(this)
    }
}