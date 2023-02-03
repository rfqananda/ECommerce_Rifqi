package com.example.ecommerce_rifqi.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.networking.APIInterface

class ProductPagingSource(private val apiService: APIInterface, private val search: String?) : PagingSource<Int, DataProduct>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataProduct> {
        Log.d("paging2", "loadPagingsource")
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getListProduct(search, position)
            LoadResult.Page(
                data = responseData.success.data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.success.data.isEmpty()) null else position + 5
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataProduct>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}