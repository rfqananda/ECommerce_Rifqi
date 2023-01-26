package com.example.ecommerce_rifqi.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListProductAdapter
import com.example.ecommerce_rifqi.databinding.FragmentHomeBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.ui.DetailActivity
import com.example.ecommerce_rifqi.ui.MyScrollListener
import com.example.ecommerce_rifqi.ui.RatingActivity
import com.example.ecommerce_rifqi.ui.view.GetListProductViewModel
import com.example.ecommerce_rifqi.utils.Communicator
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.*

class FragmentHome : Fragment(R.layout.fragment_home) {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GetListProductViewModel

    private lateinit var listProductAdapter: ListProductAdapter

    private lateinit var comm: Communicator

    lateinit var sharedPref: PreferencesHelper

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null
    private var febJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        comm = requireActivity() as Communicator

        sharedPref = PreferencesHelper(requireContext())

        setupListProduct()
        setListProduct("")

        binding.apply {
            fabHome.setOnClickListener {
                showSimpleDialog()
            }

            etSearchHome.doOnTextChanged { text, start, before, count ->
                searchJob?.cancel()

                searchJob = coroutineScope.launch{
                    text?.let {
                        showShimmer(true)
                        isDataEmpty(false)
                        delay(2000)
                        if (it.isEmpty()) {
                            viewModel.setProductList("")
                        } else {
                            viewModel.setProductList(text.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setProductList("")
        setListProduct(null)
    }

    private fun setupListProduct(){
        listProductAdapter = ListProductAdapter(requireContext())

        listProductAdapter.setOnItemClick(object : ListProductAdapter.OnAdapterListenerListProduct{
            override fun onClick(data: DataProduct) {
                val productID = data.id

                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra("id", productID)
                startActivity(intent)
            }
        })

        binding.apply {
            rvHome.layoutManager = LinearLayoutManager(context)
            rvHome.setHasFixedSize(true)
            rvHome.adapter = listProductAdapter
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

    private fun isDataEmpty(isEmpty: Boolean){
        binding.apply {
            if (isEmpty){
                animationFAB(false)
                fabHome.visibility = View.GONE
                emptyData.visibility = View.VISIBLE
            } else {
                animationFAB(true)
                fabHome.visibility = View.VISIBLE
                emptyData.visibility = View.GONE }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setListProduct(query: String?){
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[GetListProductViewModel::class.java]

        viewModel.getProductList().observe(viewLifecycleOwner){ data ->
            if (data != null){

                isDataEmpty(false)
                when (query) {
                    resources.getString(R.string.txt_sortirAZ) -> {
                        showShimmer(false)
                        listProductAdapter.setData(data.sortedBy { name ->
                            name.name_product
                        }.toList())

                    }
                    resources.getString(R.string.txt_sortirZA) -> {
                        showShimmer(false)
                        listProductAdapter.setData(data.sortedByDescending { name ->
                            name.name_product
                        }.toList())
                    }
                    else -> {
                        showShimmer(false)
                        listProductAdapter.setData(data)
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
                rvHome.visibility = View.GONE
            } else {
                shimmerList.visibility = View.GONE
                rvHome.visibility = View.VISIBLE
            }
        }
    }

    private fun animationFAB(isDataNotEmpty: Boolean){
        if (isDataNotEmpty){

            binding.fabHome.hide()

            binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    binding.fabHome.hide()
                    if (dy >= 0) {
                        febJob?.cancel()
                        febJob = coroutineScope.launch {
                            delay(1000)
                            binding.fabHome.show()
                        }
                    } else if (dy <= 0) {
                        febJob?.cancel()
                        febJob = coroutineScope.launch {
                            delay(1000)
                            binding.fabHome.show()
                        }
                    }
                }

            })
        } else binding.fabHome.hide()


    }

}