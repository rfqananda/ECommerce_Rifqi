package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.databinding.ActivityRatingBinding
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.ui.view.UpdateRateViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory

class RatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding

    private lateinit var viewModel: UpdateRateViewModel

    private lateinit var viewModelCart: GetProductCartViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            btnSubmit.setOnClickListener {
                val listProductId = intent.getStringArrayListExtra("list_id")

                val rate = ratingBar.rating
                val productID = intent.getIntExtra("id", 0)

                if (productID != 0){
                    updateRating(productID, rate.toInt())
                } else {
                    for (i in listProductId!!.indices) {
                        updateRating(listProductId[i].toInt(), rate.toInt())
                    }
                }
                viewModelCart = ViewModelProvider(this@RatingActivity)[GetProductCartViewModel::class.java]
                viewModelCart.deleteCheckedProducts()
                val intent = Intent(this@RatingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
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