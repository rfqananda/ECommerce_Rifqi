package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.databinding.ActivityRatingBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.ui.view.UpdateRateViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class RatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding

    private lateinit var viewModel: UpdateRateViewModel

    private lateinit var viewModelCart: GetProductCartViewModel

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            val paymentName = intent.getStringExtra("name")
            val paymentImage = intent.getIntExtra("image", 0)
            val totalPrice = intent.getStringExtra("total")

            tvPayment.text = paymentName
            tvPrice.text = totalPrice
            ivLogo.setImageResource(paymentImage)

            btnSubmit.setOnClickListener {

                val listProductId = intent.getStringArrayListExtra("list_id")

                val rate = ratingBar.rating
                val productID = intent.getIntExtra("id", 0)

                //On Click Button Submit
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Success")
                buttonClick.putString("button_name", "Submit")
                buttonClick.putInt("button_name", rate.toInt())
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)

                if (productID != 0){
                    updateRating(productID, rate.toInt())
                } else {
                    if (listProductId != null) {
                        for (i in listProductId.indices) {
                            updateRating(listProductId[i].toInt(), rate.toInt())
                        }
                    }
                }
                viewModelCart = ViewModelProvider(this@RatingActivity)[GetProductCartViewModel::class.java]
                
                val intent = Intent(this@RatingActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Success")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }

    private fun updateRating(productID: Int, rate: Int){
        viewModel = ViewModelProvider(this, ViewModelFactory(this))[UpdateRateViewModel::class.java]

        viewModel.setUpdateRate(productID, rate)
        viewModel.updateRateSuccess.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)
            }
        }

        viewModel.toast.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}