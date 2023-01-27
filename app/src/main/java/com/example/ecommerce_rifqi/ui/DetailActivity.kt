package com.example.ecommerce_rifqi.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.adapter.ImagePagerAdapter
import com.example.ecommerce_rifqi.databinding.ActivityDetailBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.ui.view.AddToFavoriteViewModel
import com.example.ecommerce_rifqi.ui.view.GetDetailProductViewModel
import com.example.ecommerce_rifqi.ui.view.RemoveFromFavoriteViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: GetDetailProductViewModel

    private lateinit var viewModelAddToFav: AddToFavoriteViewModel

    private lateinit var viewModelRemoveFromFav: RemoveFromFavoriteViewModel

    lateinit var sharedPref: PreferencesHelper

    private var isFavorite: Boolean = false

    private lateinit var binding: ActivityDetailBinding

    private var name: String = ""

    private var price: String = ""

    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferencesHelper(this)

        showShimmer(true)

        getDetailProductData()

        binding.apply {
            val productID = intent.getIntExtra("id", 0)
            val userID = sharedPref.getString(Constant.PREF_ID)

            btnBack.setOnClickListener {
                finish()
            }

            toggleButton.setOnClickListener {
                isFavorite = !isFavorite



                if (isFavorite) {
                    addToFav(productID!!, userID!!.toInt())
                } else {
                    removeFromFav(productID!!, userID!!.toInt())
                }
                toggleButton.isChecked = isFavorite
            }

            btnCart.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val isProductHasBeenAdded = viewModel.checkProduct(productID)
                    withContext(Dispatchers.Main) {

                        if (isProductHasBeenAdded != null){
                            if (isProductHasBeenAdded.toInt() > 0){
                                showMessage("Product Is Already In the Trolley!")
                            }else{
                                viewModel.addToCart(productID, name, price, image, 1)
                                showMessage("Product Has Been Added To Trolley!")
                            }
                        }
                    }

                }


            }
        }

    }

    private fun getDetailProductData() {

        val productID = intent.getIntExtra("id", 0)
        val userID = sharedPref.getString(Constant.PREF_ID)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[GetDetailProductViewModel::class.java]

        if (productID != null) {
            viewModel.setDetailProduct(productID, userID!!.toInt())
        }

        viewModel.getDetailProduct().observe(this) {

            if (it != null) {

                showShimmer(false)

                name = it.name_product
                price = it.harga
                image = it.image

                if (it.isFavorite) {
                    isFavorite = true
                    binding.toggleButton.isChecked = true
                } else {
                    isFavorite = false
                    binding.toggleButton.isChecked = false
                }

                binding.apply {
                    shimmerDetail.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                }

                binding.apply {
                    viewPager.adapter = ImagePagerAdapter(it.image_product)
                    springDotsIndicator.attachTo(viewPager)
                    tvProductNameHead.text = it.name_product
                    tvProductName.text = it.name_product
                    tvPrice.text = formatRupiah(it.harga.toInt())
                    ratingBar.rating = it.rate.toFloat()
                    tvStockValue.text = it.stock.toString()
                    tvSizeValue.text = it.size
                    tvWeightValue.text = it.weight
                    tvTypeValue.text = it.type
                    tvDetailValue.text = it.desc

                    btnBuy.setOnClickListener { view ->
                        sharedPref.put(Constant.PREF_ID_PRODUCT, productID.toString())
                        showBottomSheet(it)
                    }

                }
            }
        }

    }

    private fun addToFav(productID: Int, userID: Int) {
        viewModelAddToFav = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[AddToFavoriteViewModel::class.java]

        viewModelAddToFav.setAddToFav(productID, userID)
        viewModelAddToFav.addFavSuccess.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.message!!)
            }
        }

        viewModelAddToFav.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response!!)
            }
        }
    }

    private fun removeFromFav(productID: Int, userID: Int) {
        viewModelRemoveFromFav = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[RemoveFromFavoriteViewModel::class.java]

        viewModelRemoveFromFav.setRemoveFav(productID, userID)
        viewModelRemoveFromFav.removeFavSuccess.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.message)
            }
        }
        viewModelRemoveFromFav.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatRupiah(number: Int): String {
        val formatRupiah = DecimalFormat("Rp #,###")
        return formatRupiah.format(number)
    }

    private fun showShimmer(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                shimmerDetail.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
            } else {
                shimmerDetail.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }
        }
    }

    private fun showBottomSheet(dataProduct: DetailDataProduct) {
        val bottomSheetFragment = BottomSheetFragment(dataProduct)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
}