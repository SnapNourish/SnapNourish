package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {


    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val imageUploader = ImageUploader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val previewView = findViewById<PreviewView>(R.id.viewFinder)
        startCamera(previewView)

        findViewById<Button>(R.id.captureButton).setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Fail open Camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            "captured_image_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraActivity", "Image saved: $savedUri")

                    uploadPhotoToFirebase(savedUri)

                    val intent = Intent(this@CameraActivity, TransitionActivity::class.java)
                    intent.putExtra("IMAGE_URI", savedUri)
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Image capture failed: ${exception.message}")
                }
            }
        )
    }

    // helper method to call uploadPhotoAndSaveUrl from imageUploader
    private fun uploadPhotoToFirebase(fileUri: Uri){
        imageUploader.uploadPhotoAndSaveUrl(fileUri){ success->
            if(success){
                Log.d("CameraActivity", "Photo uploaded to Firebase successfully")
            }else{
                Log.e("CameraActivity", "Photo upload to Firebase failed")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

