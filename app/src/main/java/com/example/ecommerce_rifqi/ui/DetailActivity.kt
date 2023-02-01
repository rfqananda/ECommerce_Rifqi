package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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
import java.util.*

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
        val productID = intent.getIntExtra("id", 0)

        binding.apply {
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
                                val alertDialog = AlertDialog.Builder(this@DetailActivity)
                                alertDialog.apply {
                                    setTitle("Product Is Already In the Trolley")
                                    setMessage("Want to see the trolley?")
                                    setNegativeButton("Cancel") { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                    }
                                    setPositiveButton("Go to Trolley") { dialogInterface, i ->
                                        val intent = Intent(this@DetailActivity, CartActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }.show()
                            }else{
                                viewModel.addToCart(productID, name, price, image, 1, price.toInt(), false)
                                showMessage("Product Has Been Added To Trolley!")
                            }
                        }
                    }

                }
            }


            btnShare.setOnClickListener {

//                val imageShare = DownloadImageTask().execute(image)
//
//
//                val shareImage = doInBackground(image)
//
//                val path = MediaStore.Images.Media.insertImage(contentResolver, shareImage, "image desc", null)
//
//                val uri = Uri.parse(imageShare)
//
//                val intentShare = Intent(Intent.ACTION_SEND)
//                intentShare.type = "image/*"
//                intentShare.putExtra(Intent.EXTRA_TEXT, "https://myostuffsmr.com/detail_product?id=$productID")
//                intentShare.putExtra(Intent.EXTRA_STREAM, uri)
//
//                startActivity(Intent.createChooser(intentShare, "Share link using: "))

                shareDeepLink(name, binding.tvPrice.text.toString(), "https://myostuffsmr.com/detail_product?id=$productID")

            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

            swipeRefresh.setOnRefreshListener {
                showShimmer(true)
                getDetailProductData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        validationLanguage()
    }

    private fun getDetailProductData() {

        var productID = intent.getIntExtra("id", 0)
        if (productID == 0) {
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            if (id != null) {
                productID = id.toInt()
            }
        }
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

                    if (ivContainer != null) {
                        Glide.with(applicationContext)
                            .load(image)
                            .centerCrop()
                            .into(ivContainer)
                    }
                }
                binding.swipeRefresh.isRefreshing = false
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

    private fun shareDeepLink(name : String, price : String, link : String, ){
        val image = binding.ivContainer?.drawable

        val mBitmap = (image as BitmapDrawable).bitmap
        val path = MediaStore.Images.Media.insertImage(contentResolver,mBitmap, "image desc", null)

        val uri = Uri.parse(path)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_TEXT,
            "Name : "+ name +"\n"+"Price : "+ price +"\n"+"Link : "+ link
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    private fun validationLanguage(){
        val currentPost = sharedPref.getString(Constant.PREF_CURRENT_POSITION)?.toInt()
        if (currentPost == 1){
            setLocate("en")
        } else if (currentPost == 2){
            setLocate("in")
        }
    }

    private fun setLocate(Lang: String){
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

}