package com.tmp.thermaquil.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.tmp.thermaquil.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Frank","SplashAcitivty")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        findViewById<ImageView>(R.id.imgLaunch).postDelayed({
            openMainActivity()
        }, 3000)
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}