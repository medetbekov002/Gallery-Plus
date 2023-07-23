package com.example.task.presenter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.task.MainActivity
import com.example.task.databinding.ActivityPhotoBinding
import com.example.task.view.CameraActivityView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class PhotoActivity : MvpAppCompatActivity(), CameraActivityView {

    @InjectPresenter
    lateinit var presenter: CameraActivityPresenter

    private lateinit var binding: ActivityPhotoBinding

    @ProvidePresenter
    fun provideCameraActivityPresenter(): CameraActivityPresenter {
        return CameraActivityPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoBinding.inflate(layoutInflater)
        provideCameraActivityPresenter()
        presenter.attachView(this)

        setContentView(binding.root)

        // Request camera and storage permissions if not granted
        // Request camera and storage permissions if not granted
        if (allPermissionsGranted()) {
            presenter.startCamera(binding.surface)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.captureButton.setOnClickListener { presenter.onCaptureButtonClicked() }
    }

    override fun onPhotoTaken() {
        Toast.makeText(this, "Фото было успешно сделано", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onPhotoError(errorMessage: String) {
        Toast.makeText(this, "Произошла ошибка при фотографировании", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
            presenter.startCamera(binding.surface)
//            } else {
//                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
//                    .show()
//                finish()
//            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

//    override fun getPreviewSurface(): PreviewView {
//        return binding.surface
//    }
}