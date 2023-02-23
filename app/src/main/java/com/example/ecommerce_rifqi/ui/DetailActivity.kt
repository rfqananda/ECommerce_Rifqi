package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ImagePagerAdapter
import com.example.ecommerce_rifqi.adapter.ListProductDetailAdapter
import com.example.ecommerce_rifqi.databinding.ActivityDetailBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.ui.view.*
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*

class DetailActivity : AppCompatActivity(), ImagePagerAdapter.OnPageClickListener {

    private lateinit var viewModel: GetDetailProductViewModel

    private lateinit var viewModelAddToFav: AddToFavoriteViewModel

    private lateinit var viewModelRemoveFromFav: RemoveFromFavoriteViewModel

    private lateinit var viewModelOtherProduct: GetOtherProductsViewModel

    private lateinit var viewModelHistorySearch: GetProductSearchHistoryViewModel

    private lateinit var listProductAdapter: ListProductDetailAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var seePhoto: PhotoDialog

    lateinit var sharedPref: PreferencesHelper

    private var isFavorite: Boolean = false

    private lateinit var binding: ActivityDetailBinding

    private var name: String = ""

    private var price: String = ""

    private var image: String = ""

    private var stock: Int? = null

    private var imageViewPager: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listProductAdapter = ListProductDetailAdapter()

        sharedPref = PreferencesHelper(this)

        seePhoto = PhotoDialog(this)

        showShimmer(true)

        getDetailProductData()
        val productID = intent.getIntExtra("id", 0)
        val userID = sharedPref.getString(Constant.PREF_ID)

        getOtherProductData(userID!!.toInt())
        getHistoryProductData(userID.toInt())

        binding.apply {

            btnBack.setOnClickListener {
                //On Click Back Icon
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "Back Icon")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, buttonClick)
                finish()
            }

            toggleButton.setOnClickListener {
                isFavorite = !isFavorite

                if (isFavorite) {
                    addToFav(productID, userID.toInt())
                } else {
                    removeFromFav(productID, userID.toInt())
                }
                toggleButton.isChecked = isFavorite

                //On Click Love Icon
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "Love Icon")
                buttonClick.putInt("product_id", productID)
                buttonClick.putString("product_name", name)
                buttonClick.putString("status", isFavorite.toString())
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            }

            btnCart.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val isProductHasBeenAdded = viewModel.checkProduct(productID)
                    withContext(Dispatchers.Main) {
                        if (stock == 1) {
                            showMessage(resources.getString(R.string.txt_out_stock))
                        } else {
                            if (isProductHasBeenAdded != null) {
                                if (isProductHasBeenAdded.toInt() > 0) {
                                    val alertDialog = AlertDialog.Builder(this@DetailActivity)
                                    alertDialog.apply {
                                        setTitle(resources.getString(R.string.txt_product_in_cart))
                                        setMessage(resources.getString(R.string.txt_want_see))
                                        setNegativeButton(resources.getString(R.string.txt_cancel)) { dialogInterface, _ ->
                                            dialogInterface.dismiss()
                                        }
                                        setPositiveButton(resources.getString(R.string.txt_to_cart)) { _, _ ->
                                            val intent =
                                                Intent(
                                                    this@DetailActivity,
                                                    CartActivity::class.java
                                                )
                                            startActivity(intent)
                                            finish()
                                        }
                                    }.show()
                                } else {
                                    viewModel.addToCart(
                                        productID,
                                        name,
                                        price,
                                        image,
                                        1,
                                        price.toInt(),
                                        false
                                    )
                                    showMessage(resources.getString(R.string.txt_add_cart))
                                }
                            }
                        }
                    }

                }

                //On Click Button + Trolley
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "+ Trolley")
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
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

                shareDeepLink(
                    name,
                    binding.tvPrice.text.toString(),
                    "https://myostuffsmr.com/detail_product?id=$productID"
                )

                //On Click Share Product
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("product_name", name)
                buttonClick.putString("product_price", price)
                buttonClick.putInt("product_id", productID)
                buttonClick.putString("button_name", "Share Product")
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            }

            btnBack.setOnClickListener {
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "Back Icon")
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
                onBackPressed()
            }

            swipeRefresh.setOnRefreshListener {
                showShimmer(true)
                getDetailProductData()
            }

            rvOtherProduct!!.layoutManager = LinearLayoutManager(this@DetailActivity)
            rvOtherProduct.setHasFixedSize(true)
            rvOtherProduct.adapter = listProductAdapter

            rvHistorySearchProduct!!.layoutManager = LinearLayoutManager(this@DetailActivity)
            rvHistorySearchProduct.setHasFixedSize(true)
            rvHistorySearchProduct.adapter = listProductAdapter
        }


    }

    override fun onStart() {
        super.onStart()
        validationLanguage()
        isDataShowing(false)
    }

    override fun onResume() {
        super.onResume()
        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Detail Product")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sharedPref.deletePayment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        viewModel.setDetailProduct(productID, userID!!.toInt())

        viewModel.getDetailProduct().observe(this) {

            if (it != null) {
                showShimmer(false)

                name = it.name_product
                price = it.harga
                image = it.image
                stock = it.stock

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
                    isDataShowing(true)
                    viewPager.adapter = ImagePagerAdapter(it.image_product, this@DetailActivity)
                    springDotsIndicator.attachTo(viewPager)

                    tvProductNameHead.text = it.name_product
                    tvProductName.text = it.name_product
                    tvPrice.text = formatRupiah(it.harga.toInt())
                    ratingBar.rating = it.rate.toFloat()
                    tvSizeValue.text = it.size
                    tvWeightValue.text = it.weight
                    tvTypeValue.text = it.type
                    tvDetailValue.text = it.desc
                    if (it.stock == 1) {
                        tvStockValue.text = resources.getString(R.string.txt_out_stock)
                    } else tvStockValue.text = it.stock.toString()

                    tvProductName.setOnClickListener {
                        Log.e("View Pager Value", imageViewPager)
                    }

                    toolBarDp.isSelected = true

                    val paymentName = intent.getStringExtra("name")
                    val paymentImage = intent.getIntExtra("image", 0)

                    Log.e("PaymentName", paymentName.toString())
                    Log.e("PaymentImage", paymentImage.toString())

                    if (paymentName != null) {
                        if (name.isNotEmpty()){
                            showBottomSheet(it, paymentName, paymentImage)
                        }
                    }

                    btnBuy.setOnClickListener { _ ->
                        sharedPref.put(Constant.PREF_ID_PRODUCT, productID.toString())
                        if (paymentName != null) {
                            if (paymentName.isNotEmpty()){
                                showBottomSheet(it, paymentName, paymentImage)
                            }
                        } else showBottomSheet(it, null, null)

                        //On Click Button Buy
                        firebaseAnalytics = Firebase.analytics
                        val buttonClick = Bundle()
                        buttonClick.putString("screen_name", "Detail Product")
                        buttonClick.putString("button_name", "Buy")
                        firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
                    }

                    Glide.with(applicationContext)
                        .load(image)
                        .centerCrop()
                        .into(ivContainer)
                }
                binding.swipeRefresh.isRefreshing = false
            }
        }

    }

    private fun getOtherProductData(id_user: Int) {
        viewModelOtherProduct = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[GetOtherProductsViewModel::class.java]
        viewModelOtherProduct.setOtherProductList(id_user)
        viewModelOtherProduct.getOtherProductList().observe(this) {
            if (it != null) {

                if (it.isNotEmpty()){
                    listProductAdapter.setData(it)
                } else {
                    binding.apply {
                        line2?.visibility = View.GONE
                        headerOtherProduct?.visibility = View.GONE
                        rvOtherProduct?.visibility = View.GONE
                    }
                }

//                isDataOtherEmpty(false)
//                if (it.isEmpty()){
//                    isDataOtherEmpty(true)
//                }
            }
        }
    }

    private fun getHistoryProductData(id_user: Int) {
        viewModelHistorySearch = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[GetProductSearchHistoryViewModel::class.java]
        viewModelHistorySearch.setHistoryProductList(id_user)
        viewModelHistorySearch.getHistoryProductList().observe(this) {
            if (it != null) {
                if (it.isNotEmpty()){
                    listProductAdapter.setData(it)
                } else {
                    binding.apply {
                        line3?.visibility = View.GONE
                        headerSearchHistory?.visibility = View.GONE
                        rvHistorySearchProduct?.visibility = View.GONE
                    }
                }
//                isDataHistoryEmpty(false)
//                if (it.isEmpty()){
//                    isDataHistoryEmpty(true)
//                }
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
                showMessage(response.message)
            }
        }

        viewModelAddToFav.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
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

    private fun showBottomSheet(dataProduct: DetailDataProduct, name: String?, image: Int?) {
        val bottomSheetFragment = BottomSheetFragment(dataProduct, name, image)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        //On Show Popup
        firebaseAnalytics = Firebase.analytics
        val popupDetail = Bundle()
        popupDetail.putString("screen_name", "Detail Product")
        popupDetail.putString("popup", "show")
        popupDetail.putInt("product_id", dataProduct.id)
        firebaseAnalytics.logEvent(Constant.popup_detail, popupDetail)
    }

    private fun shareDeepLink(name: String, price: String, link: String) {

        val image = binding.ivContainer.drawable

        val mBitmap = (image as BitmapDrawable).bitmap
        val path = MediaStore.Images.Media.insertImage(contentResolver, mBitmap, "image desc", null)

        val uri = Uri.parse(path)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "${resources.getString(R.string.txt_laptop)}: $name\n${resources.getString(R.string.txt_price)} : $price\nLink : $link"
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.txt_laptop)))
    }

    private fun validationLanguage() {
        val currentPost = sharedPref.getString(Constant.PREF_CURRENT_POSITION)?.toInt()
        if (currentPost == 1) {
            setLocate("en")
        } else if (currentPost == 2) {
            setLocate("in")
        }
    }

    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    override fun onClick(image: String) {
        seePhoto.showPhoto(image)
        imageViewPager = image
    }


    private fun isDataShowing(isShowing: Boolean) {
        binding.apply {
            if (isShowing) {
                btnShare.visibility = View.VISIBLE
                btnBuy.visibility = View.VISIBLE
                btnCart.visibility = View.VISIBLE
            } else {
                btnShare.visibility = View.GONE
                btnBuy.visibility = View.GONE
                btnCart.visibility = View.GONE
            }
        }
//    private fun isDataOtherEmpty(isEmpty: Boolean){
//        binding.apply {
//            if (isEmpty){
//                emptyData!!.visibility = View.VISIBLE
//            } else {
//                emptyData!!.visibility = View.GONE }
//        }
//    }

//    private fun isDataHistoryEmpty(isEmpty: Boolean){
//        binding.apply {
//            if (isEmpty){
//                emptyData2!!.visibility = View.VISIBLE
//            } else {
//                emptyData2!!.visibility = View.GONE }
//        }
//    }

    }
}