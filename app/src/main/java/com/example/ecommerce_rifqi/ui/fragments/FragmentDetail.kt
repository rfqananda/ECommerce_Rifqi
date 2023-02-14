package com.example.ecommerce_rifqi.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ImagePagerAdapter
import com.example.ecommerce_rifqi.databinding.FragmentDetailBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.ui.BottomSheetFragment
import com.example.ecommerce_rifqi.ui.view.AddToFavoriteViewModel
import com.example.ecommerce_rifqi.ui.view.GetDetailProductViewModel
import com.example.ecommerce_rifqi.ui.view.RemoveFromFavoriteViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import java.text.DecimalFormat

class FragmentDetail : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GetDetailProductViewModel

    private lateinit var viewModelAddToFav: AddToFavoriteViewModel

    private lateinit var viewModelRemoveFromFav: RemoveFromFavoriteViewModel



    lateinit var sharedPref: PreferencesHelper

    private var isFavorite: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)

        sharedPref = PreferencesHelper(requireContext())

        showShimmer(true)

        getDetailProductData()

        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentDetail_to_fragmentHome)
            }


            toggleButton.setOnClickListener {
                isFavorite = !isFavorite

                val productID = arguments?.getInt("id")
                val userID = sharedPref.getString(Constant.PREF_ID)

                if (isFavorite) {
                    addToFav(productID!!, userID!!.toInt())
                } else {
                    removeFromFav(productID!!, userID!!.toInt())
                }
                toggleButton.isChecked = isFavorite
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun getDetailProductData() {

        val productID = arguments?.getInt("id")
        val userID = sharedPref.getString(Constant.PREF_ID)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[GetDetailProductViewModel::class.java]

        if (productID != null) {
            viewModel.setDetailProduct(productID, userID!!.toInt())
        }

        viewModel.getDetailProduct().observe(viewLifecycleOwner) {

            if (it != null) {

                showShimmer(false)

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
//                    viewPager.adapter = ImagePagerAdapter(it.image_product, this@FragmentDetail)
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

                    }

                }
            }
        }

    }

    private fun addToFav(productID: Int, userID: Int) {
        viewModelAddToFav = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[AddToFavoriteViewModel::class.java]

        viewModelAddToFav.setAddToFav(productID, userID)
        viewModelAddToFav.addFavSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.message!!)
            }
        }

        viewModelAddToFav.toast.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response!!)
            }
        }
    }

    private fun removeFromFav(productID: Int, userID: Int) {
        viewModelRemoveFromFav = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[RemoveFromFavoriteViewModel::class.java]

        viewModelRemoveFromFav.setRemoveFav(productID, userID)
        viewModelRemoveFromFav.removeFavSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.message)
            }
        }
        viewModelRemoveFromFav.toast.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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


}