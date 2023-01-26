package com.example.ecommerce_rifqi.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.TranslateAnimation
import com.example.ecommerce_rifqi.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)

    }

    private fun playAnimation() {
        val image = binding.ivSplash
        ObjectAnimator.ofFloat(image, View.TRANSLATION_Y, 0f, -800f).apply {
            duration = 3000
        }.start()
    }
}