package com.dicoding.handspeak.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dicoding.handspeak.R
import com.dicoding.handspeak.ui.LoginActivity as ComDicodingHandSpeakMainActivity


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, ComDicodingHandSpeakMainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}