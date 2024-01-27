package com.example.vero

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class CameraActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCameraScanner()
    }

    private fun startCameraScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)  // Ekran döndürmeyi engelle
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // Tarama iptal edildi
                // İşlemlerinizi burada gerçekleştirin
            } else {
                // QR kodu başarıyla tarandı, result.contents içerir
                // İşlemlerinizi burada gerçekleştirin
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
