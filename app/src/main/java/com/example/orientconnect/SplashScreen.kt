package com.example.orientconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        val backgroundImg : ImageView = findViewById(R.id.SplashLogo)
        val sideAnimation = AnimationUtils.loadAnimation(this,R.anim.splashanim)
        backgroundImg.startAnimation(sideAnimation)

        Handler().postDelayed({
            val intent= Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()

        },3000)
    }
}