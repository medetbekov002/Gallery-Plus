package com.example.task.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.task.model.PhotoDbHelper
import java.io.File

class CleanupWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        return try {
            cleanupFiles()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun cleanupFiles() {
        val currentTime = System.currentTimeMillis()
        val twoHours = 2 * 60 * 60 * 1000
//        val fiveMin = 5 * 60 * 1000
//        val twoMin = 2 * 60 * 1000

        val dbHelper = PhotoDbHelper(context = applicationContext)
        val photos = dbHelper.getInvalidPhotos(currentTime - twoHours)

        photos.forEach { photo ->
            val file = File(photo.path)
            if (file.exists()) {
                file.delete()
                dbHelper.removePhoto(photo.id)
            }
        }
    }

}