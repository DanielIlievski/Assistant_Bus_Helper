package com.example.assistive_bus_helper

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.assistive_bus_helper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // region activity variables
    private lateinit var binding: ActivityMainBinding

    private var imgUri: Uri? = null

    private val requestCameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                takeImage()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("HelloWorld", "takeImageResult: $imgUri")
            }
        }
    // endregion

    // region activity methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onImgCameraClickListener()
    }
    // endregion

    // region onImgCameraClickListener
    private fun onImgCameraClickListener() {
        binding.imgCamera.setOnClickListener {
            if (hasPermission()) {
                takeImage()
            } else {
                requestCameraPermissionResult.launch(CAMERA_PERMISSION)
            }
        }
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun takeImage() {
        initImgUri()

        val cameraIntent = Intent().apply {
            action = MediaStore.ACTION_IMAGE_CAPTURE
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        takeImageResult.launch(cameraIntent)
    }

    private fun initImgUri() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imgUri = this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }
    // endregion

    companion object {
        const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    }
}