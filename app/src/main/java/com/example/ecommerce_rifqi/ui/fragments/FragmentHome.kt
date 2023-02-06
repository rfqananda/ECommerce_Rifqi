package com.example.ecommerce_rifqi.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListProductAdapter
import com.example.ecommerce_rifqi.adapter.LoadingStateAdapter
import com.example.ecommerce_rifqi.databinding.FragmentHomeBinding
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.paging.ProductPagingSource
import com.example.ecommerce_rifqi.ui.DetailActivity
import com.example.ecommerce_rifqi.ui.view.GetListProductViewModel
import com.example.ecommerce_rifqi.ui.view.ViewModelFactoryProduct
import com.example.ecommerce_rifqi.utils.Communicator
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        comm = requireActivity() as Communicator

        sharedPref = PreferencesHelper(requireContext())

        val factory = ViewModelFactoryProduct(requireContext().applicationContext)
        viewModel = ViewModelProvider(this, factory)[GetListProductViewModel::class.java]

        getListProduct(null)

        binding.apply {

            etSearchHome.doOnTextChanged { text, start, before, count ->
                searchJob?.cancel()

                searchJob = coroutineScope.launch{
                    text?.let {
                        isDataEmpty(false)
                        delay(2000)
                        if (it.isEmpty()) {
                            getListProduct(null)
                        } else {
                            getListProduct(text.toString())
                        }
                    }
                }
            }

            swipeRefresh.setOnRefreshListener{
                getListProduct("")
                searchJob?.cancel()
                etSearchHome.text?.clear()
                etSearchHome.clearFocus()
                swipeRefresh.isRefreshing = false
            }
        }

    }


    private fun isDataEmpty(isEmpty: Boolean){
        binding.apply {
            if (isEmpty){
                emptyData.visibility = View.VISIBLE
            } else {
                emptyData.visibility = View.GONE }
        }
    }


    private fun getListProduct(query: String?){
        view?.let {
            listProductAdapter = ListProductAdapter()
            binding.rvHome.adapter = listProductAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    listProductAdapter.retry()
                }
            )

            listProductAdapter.addLoadStateListener { loadState ->
                showShimmer(loadState.refresh is LoadState.Loading)
                binding.swipeRefresh.isEnabled = loadState.source.refresh is LoadState.NotLoading

                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listProductAdapter.itemCount < 1){
                    isDataEmpty(true)
                } else isDataEmpty(false)
            }

            val lifecycleOwner = viewLifecycleOwner
            viewModel.productListPaging(query).observe(lifecycleOwner){
                if (it != null){
                    isDataEmpty(false)
                    listProductAdapter.submitData(lifecycle, it)
                    listProductAdapter.setOnItemClick(object : ListProductAdapter.OnAdapterListenerListProduct{
                        override fun onClick(data: DataProduct) {
                            val productID = data.id

                            val intent = Intent(requireActivity(), DetailActivity::class.java)
                            intent.putExtra("id", productID)
                            startActivity(intent)
                        }
                    })
                    binding.swipeRefresh.isRefreshing = false

                } else isDataEmpty(true)
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

    override fun onDetach() {
        super.onDetach()
        searchJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        searchJob?.cancel()
        getListProduct(null)
    }


    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
    }

}