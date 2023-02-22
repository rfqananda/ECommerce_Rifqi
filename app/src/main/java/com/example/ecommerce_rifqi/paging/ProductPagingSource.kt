package com.example.ecommerce_rifqi.paging

import android.os.Bundle
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.networking.APIInterface
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class ProductPagingSource(private val apiService: APIInterface, private val search: String?) : PagingSource<Int, DataProduct>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 0
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataProduct> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getListProduct(search, position)

            //Firebase On Paging Scroll
            firebaseAnalytics = Firebase.analytics
            val onScroll = Bundle()
            onScroll.putString("screen_name", "Home")
            onScroll.putInt("page", position)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onScroll)
            Log.e("DataPageOffset", "Page = $position")

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