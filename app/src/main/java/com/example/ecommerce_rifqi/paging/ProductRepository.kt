package com.example.ecommerce_rifqi.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.networking.APIInterface

class ProductRepository(private val apiService: APIInterface) {
    fun getProduct(search : String?): LiveData<PagingData<DataProduct>> {

        return Pager(
            config = PagingConfig(
                pageSize = 5,
                prefetchDistance = 1,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                ProductPagingSource(apiService, search)
            }
        ).liveData
    }
}