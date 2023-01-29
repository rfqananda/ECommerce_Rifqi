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
import com.example.ecommerce_rifqi.ui.fragments.FragmentDetail
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.utils.Communicator
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ActivityMainBinding

    private lateinit var badge: BadgeDrawable

    private var badgeNumber = 0

    lateinit var sharedPref: PreferencesHelper

    private lateinit var viewModel: GetProductCartViewModel


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
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }

    override fun onStart() {
        super.onStart()
        validationLanguage()
        countData()
    }

    private fun countData(){
        viewModel = ViewModelProvider(this)[GetProductCartViewModel::class.java]
        viewModel.getProductCount()
        viewModel.totalData.observe(this){
            if (it > 0){
                binding.badgeCount.visibility = View.VISIBLE
                binding.badgeCount.text = it.toString()
            } else binding.badgeCount.visibility = View.GONE
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