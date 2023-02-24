package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.adapter.CobaAdapter1
import com.example.ecommerce_rifqi.adapter.CobaAdapter2
import com.example.ecommerce_rifqi.adapter.PaymentMethodAdapter
import com.example.ecommerce_rifqi.databinding.ActivityPaymentBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataPayment
import com.example.ecommerce_rifqi.model.PaymentMethod
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PaymentActivity : AppCompatActivity(), CobaAdapter1.OnAdapterListenerPaymentList {
    private lateinit var binding: ActivityPaymentBinding

    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var payment_remote_config: String? = null

    lateinit var sharedPref: PreferencesHelper

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var paymentAdapter: PaymentMethodAdapter

    private lateinit var cobaAdapter2: CobaAdapter2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferencesHelper(this)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                onFirebaseActivated()
            }
        }

        binding.btnBack.setOnClickListener {
            //On Click Back Icon
            firebaseAnalytics = Firebase.analytics
            val buttonClick = Bundle()
            buttonClick.putString("screen_name", "Pilih Metode Pembayaran")
            buttonClick.putString("button_name", "Back Icon")
            firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Pilih Metode Pembayaran")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }

    private fun onFirebaseActivated() {
        payment_remote_config = remoteConfig.getString("payment_json")
        val dataList = Gson().fromJson<ArrayList<PaymentMethod>>(
            payment_remote_config,
            object : TypeToken<ArrayList<PaymentMethod>>() {}.type
        )
        Log.d("paymentMethod", dataList.toString())
//        paymentAdapter = PaymentMethodAdapter(this)
//        paymentAdapter.setData(dataList)
        cobaAdapter2 = CobaAdapter2(this)
        cobaAdapter2.setData(dataList as List<PaymentMethod>)
//        binding.rvPayment.adapter = paymentAdapter

        binding.rvPayment.adapter = cobaAdapter2

        binding.rvPayment.layoutManager = LinearLayoutManager(this)
        binding.rvPayment.setHasFixedSize(true)
    }

    override fun onClick(data: DataPayment, drawable: Int) {
        //On Click Bank
        val jenisPembayaran = if (data.name!!.contains("Virtual Account")) {
            "Virtual Account"
        } else {
            "E-Wallet"
        }
        firebaseAnalytics = Firebase.analytics
        val buttonClick = Bundle()
        buttonClick.putString("screen_name", "Pilih Metode Pembayaran")
        buttonClick.putString("jenis_pembayaran", jenisPembayaran)
        buttonClick.putString("bank", data.name)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, buttonClick)

        val productID = intent.getIntExtra("productID", 0)

        if (productID == 0){
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("name", data.name)
            intent.putExtra("image", drawable)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)


            startActivity(intent)
            finish()
        } else{
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("name", data.name)
            intent.putExtra("image", drawable)
            intent.putExtra("id", productID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
            finish()
        }
        onBackPressed()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}