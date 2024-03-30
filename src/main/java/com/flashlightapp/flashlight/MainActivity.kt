package com.flashlightapp.flashlight
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatDelegate
class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashOn: Boolean = false
    private lateinit var toggleButton: ImageButton
    private lateinit var themeToggleButton: ToggleButton
    private var isDarkTheme: Boolean = false
    private lateinit var sunIcon: Drawable
    private lateinit var moonIcon: Drawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggleButton = findViewById(R.id.toggle_button)
        themeToggleButton = findViewById(R.id.theme_toggle_button)
        sunIcon = ContextCompat.getDrawable(this, R.drawable.light_mode)!!
        moonIcon = ContextCompat.getDrawable(this, R.drawable.dark_mode)!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        toggleButton.setOnClickListener {
            if (isFlashOn) {
                turnOffFlash()
            } else {
                turnOnFlash()
            }
        }
        themeToggleButton.setOnCheckedChangeListener { _, isChecked ->
            isDarkTheme = isChecked
            applyTheme(isDarkTheme)
            updateThemeToggleIcon(isDarkTheme)
        }
        updateThemeToggleIcon(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
    }
    private fun turnOnFlash() {
        try {
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId!!, true)
                isFlashOn = true
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    private fun turnOffFlash() {
        try {
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId!!, false)
                isFlashOn = false
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    private fun applyTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.decorView.setBackgroundColor(Color.rgb(0, 0, 0))
            toggleButton.setColorFilter(Color.rgb(255, 255, 204))
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.decorView.setBackgroundColor(Color.WHITE)
            toggleButton.setColorFilter(Color.rgb(0, 51, 102))
        }
    }
    private fun updateThemeToggleIcon(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            themeToggleButton.setCompoundDrawablesWithIntrinsicBounds(moonIcon, null, null, null)
        } else {
            themeToggleButton.setCompoundDrawablesWithIntrinsicBounds(sunIcon, null, null, null)
        }
    }
    override fun onStop() {
        super.onStop()
        if (isFlashOn) {
            turnOffFlash()
        }
    }
}