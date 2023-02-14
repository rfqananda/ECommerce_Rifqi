package com.example.ecommerce_rifqi.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.PaymentMethodAdapter
import com.example.ecommerce_rifqi.databinding.ActivityPaymentBinding
import com.example.ecommerce_rifqi.databinding.FragmentChangePasswordBinding
import com.example.ecommerce_rifqi.model.DataPayment
import com.example.ecommerce_rifqi.model.PaymentMethod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FragmentPayment: Fragment(R.layout.activity_payment), PaymentMethodAdapter.OnPaymentMethodClickListener {
    private var _binding: ActivityPaymentBinding? = null
    private val binding get() = _binding!!

    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var payment_remote_config: String? = null

    private lateinit var paymentAdapter: PaymentMethodAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityPaymentBinding.bind(view)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }


        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                onFirebaseActivated()
            }
        }
    }

    private fun onFirebaseActivated() {
        payment_remote_config = remoteConfig.getString("payment_json")
        val dataList = Gson().fromJson<ArrayList<PaymentMethod>>(
            payment_remote_config,
            object : TypeToken<ArrayList<PaymentMethod>>() {}.type
        )
        Log.d("paymentMethod", dataList.toString())
        paymentAdapter = PaymentMethodAdapter(this)
        paymentAdapter.setData(dataList)
        binding.rvPayment.adapter = paymentAdapter
        binding.rvPayment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPayment.setHasFixedSize(true)
    }

    override fun onPaymentMethodClick(data: DataPayment, drawable: Int) {
        val bundle = Bundle()
        bundle.putString("name", data.name)
        bundle.putInt("image", drawable)

        findNavController().navigate(R.id.action_fragmentPayment_to_bottomSheetFragment, bundle)
    }


}