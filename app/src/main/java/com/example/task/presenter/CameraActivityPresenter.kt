package com.example.task.presenter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.task.application.MyApplication
import com.example.task.model.PhotoDbHelper
import com.example.task.view.CameraActivityView
import moxy.MvpPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.io.File
import java.util.Date
import java.util.Locale

@StateStrategyType(OneExecutionStateStrategy::class)
class CameraActivityPresenter(private val context: Context) : MvpPresenter<CameraActivityView>() {
    private val dbHelper: PhotoDbHelper = MyApplication.databaseHelper

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture

    fun onCaptureButtonClicked() {
        val file = createOutputFile()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(file)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(file.lastModified()))
                    viewState?.onPhotoTaken()
                    Log.d(TAG, "Photo saved: ${file.absolutePath}, Date: $date")

                    savePhotoToDb(file.absolutePath, file.lastModified())
                }

                override fun onError(exception: ImageCaptureException) {
                    viewState?.onPhotoError("Error capturing photo: ${exception.message}")
                    Log.e(TAG, "Error capturing photo: ${exception.message}")
                }
            })
    }

    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Set up the image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Select the back camera as the default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (ex: Exception) {
                viewState?.onPhotoError("Error starting camera: ${ex.message}")
                Log.e(TAG, "Error starting camera: ${ex.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun createOutputFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}",
            ".jpg",
            storageDir
        )
    }

    companion object {
        private const val TAG = "CameraXApp"
    }

    private fun savePhotoToDb(imagePath: String, creationDate: Long) {
        dbHelper.insertPhoto(
            imagePath,
            creationDate
        )
    }

}