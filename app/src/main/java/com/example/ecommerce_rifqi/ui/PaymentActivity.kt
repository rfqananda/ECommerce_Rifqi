package com.example.ecommerce_rifqi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.adapter.ListProductAdapter
import com.example.ecommerce_rifqi.adapter.PaymentMethodAdapter
import com.example.ecommerce_rifqi.databinding.ActivityPaymentBinding
import com.example.ecommerce_rifqi.model.PaymentMethod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var payment_remote_config: String? = null

    private lateinit var paymentAdapter: PaymentMethodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }


        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                onFirebaseActivated()
            }
        }
    }

    fun onFirebaseActivated() {
        payment_remote_config = remoteConfig.getString("payment_json")
        val dataList = Gson().fromJson<ArrayList<PaymentMethod>>(
            payment_remote_config,
            object : TypeToken<ArrayList<PaymentMethod>>() {}.type
        )
        Log.d("paymentMethod", dataList.toString())
        paymentAdapter = PaymentMethodAdapter()
        paymentAdapter.setData(dataList)
        binding.rvPayment.adapter = paymentAdapter
        binding.rvPayment.layoutManager = LinearLayoutManager(this)
        binding.rvPayment.setHasFixedSize(true)
    }
}