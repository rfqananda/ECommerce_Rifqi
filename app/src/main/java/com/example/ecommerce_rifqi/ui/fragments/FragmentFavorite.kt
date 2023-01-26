package com.example.ecommerce_rifqi.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListProductAdapter
import com.example.ecommerce_rifqi.adapter.ListProductFavoriteAdapter
import com.example.ecommerce_rifqi.databinding.FragmentFavoriteBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.ui.DetailActivity
import com.example.ecommerce_rifqi.ui.MyScrollListener
import com.example.ecommerce_rifqi.ui.view.GetListFavoriteProductViewModel
import com.example.ecommerce_rifqi.ui.view.GetListProductViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

class FragmentFavorite : Fragment(R.layout.fragment_favorite) {

    private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedPref: PreferencesHelper

    private lateinit var listFavoriteProductAdapter: ListProductFavoriteAdapter

    private lateinit var viewModel: GetListFavoriteProductViewModel

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        sharedPref = PreferencesHelper(requireContext())

        setupListProduct()
        setListProduct("")

        binding.apply {

            rvFavorite.addOnScrollListener(MyScrollListener(fabFavorite))


            fabFavorite.setOnClickListener {
                showSimpleDialog()
            }

            etSearchFavorite.doOnTextChanged { text, start, before, count ->
                searchJob?.cancel()

                val userID = sharedPref.getString(Constant.PREF_ID)

                searchJob = coroutineScope.launch{
                    text?.let {
                        showShimmer(true)
                        isDataEmpty(false)
                        delay(2000)
                        if (it.isEmpty()) {
                            viewModel.setFavoriteProductList("", userID!!.toInt())
                        } else {
                            viewModel.setFavoriteProductList(text.toString(), userID!!.toInt())
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setFavoriteProductList("", sharedPref.getString(Constant.PREF_ID)!!.toInt())
        setListProduct(null)
    }

    private fun isDataEmpty(isEmpty: Boolean){
        binding.apply {
            if (isEmpty){
                fabFavorite.visibility = View.GONE
                emptyData.visibility = View.VISIBLE
            } else {
                fabFavorite.visibility = View.VISIBLE
                emptyData.visibility = View.GONE }
        }
    }

    private fun showSimpleDialog(){
        val options = arrayOf(resources.getString(R.string.txt_sortirAZ), resources.getString(R.string.txt_sortirZA))
        var selectedOption = ""

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.txt_sort))
            .setSingleChoiceItems(options, -1){ _, which ->
                selectedOption = options[which]
            }
            .setPositiveButton("Ok"){ _, _->
                when (selectedOption) {
                    resources.getString(R.string.txt_sortirAZ) -> setListProduct(resources.getString(R.string.txt_sortirAZ))
                    resources.getString(R.string.txt_sortirZA) -> setListProduct(resources.getString(R.string.txt_sortirZA))
                }
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }

            .show()
    }

    private fun setupListProduct(){
        listFavoriteProductAdapter = ListProductFavoriteAdapter(requireContext())
        listFavoriteProductAdapter.setOnItemClick(object : ListProductFavoriteAdapter.OnAdapterListenerListProductFavorite{
            override fun onClick(data: DataProduct) {
                val productID = data.id

                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra("id", productID)
                startActivity(intent)
            }
        })

        binding.apply {
            rvFavorite.layoutManager = LinearLayoutManager(context)
            rvFavorite.setHasFixedSize(true)
            rvFavorite.adapter = listFavoriteProductAdapter
        }
    }

    private fun setListProduct(query: String?){
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[GetListFavoriteProductViewModel::class.java]

        viewModel.getFavoriteProductList().observe(viewLifecycleOwner){ data ->
            if (data != null){
                isDataEmpty(false)
                when (query) {
                    resources.getString(R.string.txt_sortirAZ) -> {
                        showShimmer(false)
                        listFavoriteProductAdapter.setData(data.sortedBy { name ->
                            name.name_product
                        }.toList())

                    }
                    resources.getString(R.string.txt_sortirZA) -> {
                        showShimmer(false)
                        listFavoriteProductAdapter.setData(data.sortedByDescending { name ->
                            name.name_product
                        }.toList())
                    }
                    else -> {
                        showShimmer(false)
                        listFavoriteProductAdapter.setData(data)
                    }
                }

                if (data.isEmpty()){
                    isDataEmpty(true)
                }
            }
        }
    }

    private fun showShimmer(isLoading: Boolean){
        binding.apply {
            if (isLoading){
                shimmerList.visibility = View.VISIBLE
                rvFavorite.visibility = View.GONE
            } else {
                shimmerList.visibility = View.GONE
                rvFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}