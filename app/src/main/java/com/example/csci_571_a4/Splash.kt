package com.example.csci_571_a4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("Splash", "Navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Closes the SplashActivity so it's not in the back stack
        }, 2000) // 2000 milliseconds = 2 seconds
    }
}