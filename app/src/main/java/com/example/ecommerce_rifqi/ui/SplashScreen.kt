package com.example.ecommerce_rifqi.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import com.example.ecommerce_rifqi.databinding.ActivitySplashScreenBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    lateinit var sharedPref: PreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferencesHelper(this)

        playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            getTokenFirebase()
            finish()
        }, 2500)

    }

    private fun playAnimation() {
        val image = binding.ivSplash
        ObjectAnimator.ofFloat(image, View.TRANSLATION_Y, 0f, -800f).apply {
            duration = 3000
        }.start()
    }

    private fun getTokenFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            sharedPref.put(Constant.PREF_FB, token)
            Log.d("tokenfirebase", token)
        }
    }
}