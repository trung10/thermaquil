package com.tmp.thermaquil.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tmp.thermaquil.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("SplashActivity", "SplashAcitivty")
        super.onCreate(savedInstanceState)

        runnable = Runnable {
            openMainActivity()
        }

        setContentView(R.layout.activity_splash)
        findViewById<AppCompatButton>(R.id.btnStart).postDelayed(runnable, 3000)

        findViewById<AppCompatButton>(R.id.btnStart).setOnClickListener {
            it.removeCallbacks(runnable)
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}