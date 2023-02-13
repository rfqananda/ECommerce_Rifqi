package com.example.ecommerce_rifqi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ecommerce_rifqi.databinding.ActivityPaymentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var dataRemoteConfig: String? = null
    var payment_remote_config: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }


        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                onFirebaseActivated()
                Log.e("PaymentFB", payment_remote_config.toString())
            }
        }
    }

    fun onFirebaseActivated(){
        payment_remote_config = remoteConfig.getString("payment")
    }
}