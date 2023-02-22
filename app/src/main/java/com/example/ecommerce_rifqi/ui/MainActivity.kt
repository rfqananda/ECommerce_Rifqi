package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.ActivityMainBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.ui.view.NotificationViewModel
import com.example.ecommerce_rifqi.utils.Communicator
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ActivityMainBinding

    private lateinit var badge: BadgeDrawable

    private var badgeNumber = 0

    lateinit var sharedPref: PreferencesHelper

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var viewModelCart: GetProductCartViewModel

    private lateinit var viewModelNotification: NotificationViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = PreferencesHelper(this)

        binding.apply {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            bottomNavigation.setupWithNavController(fragment!!.findNavController())

            fragment.findNavController().addOnDestinationChangedListener{ _, destination, _ ->
                when(destination.id){
                    R.id.fragmentHome, R.id.fragmentFavorite, R.id.fragmentProfile ->
                        bottomNavigation.visibility = View.VISIBLE
                    else -> bottomNavigation.visibility = View.GONE
                }

                when(destination.id){
                    R.id.fragmentChangePassword, R.id.fragmentDetail -> toolBar.visibility = View.GONE
                    else -> toolBar.visibility = View.VISIBLE
                }
            }

            btnCart.setOnClickListener {
                val intent = Intent(this@MainActivity, CartActivity::class.java)
                startActivity(intent)

                //Firebase OnClick Trolley Icon
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Home")
                buttonClick.putString("button_name", "Trolley Icon")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, buttonClick)
            }

            btnNotification.setOnClickListener {
                val intent = Intent(this@MainActivity, NotificationActivity::class.java)
                startActivity(intent)

                //Firebase OnClick Notification Icon
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Home")
                buttonClick.putString("button_name", "Notif Icon")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, buttonClick)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }

    override fun onStart() {
        super.onStart()
        validationLanguage()
        countDataProduct()
        countDataNotification()
        val title = sharedPref.getString(Constant.PREF_GET_TITLE)
        val message = sharedPref.getString(Constant.PREF_GET_MESSAGE)
        Log.e("Title", title.toString())
        Log.e("Message", message.toString())

    }

    private fun countDataProduct(){
        viewModelCart = ViewModelProvider(this)[GetProductCartViewModel::class.java]
        viewModelCart.getProductCount()
        viewModelCart.totalData.observe(this){
            if (it > 0){
                binding.badgeCountCart.visibility = View.VISIBLE
                binding.badgeCountCart.text = it.toString()
            } else binding.badgeCountCart.visibility = View.GONE
        }

    }

    private fun countDataNotification(){
        viewModelNotification = ViewModelProvider(this)[NotificationViewModel::class.java]
        viewModelNotification.getNotificationCount()
        viewModelNotification.totalData.observe(this){
            if (it > 0){
                binding.badgeCountNotification.visibility = View.VISIBLE
                binding.badgeCountNotification.text = it.toString()
            } else binding.badgeCountNotification.visibility = View.GONE
        }

    }


    private fun setLocate(Lang: String){
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun validationLanguage(){
        val currentPost = sharedPref.getString(Constant.PREF_CURRENT_POSITION)?.toInt()
        if (currentPost == 1){
            setLocate("en")
        } else if (currentPost == 2){
            setLocate("in")
        }
    }

    override fun passDataCom(image: String, price: String, stock: Int) {
        val bundle = Bundle()
        bundle.putString("image", image)
    }

}