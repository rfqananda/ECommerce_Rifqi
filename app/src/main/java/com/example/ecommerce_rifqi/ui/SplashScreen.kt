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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    lateinit var sharedPref: PreferencesHelper

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferencesHelper(this)




//        this.javaClass.simpleName
        playAnimation()



        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            firebaseAnalytics = Firebase.analytics
            val param = Bundle()
            param.putString("screen_name", "Splash Screen")
            param.putString("screen_class", this.javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, param)
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