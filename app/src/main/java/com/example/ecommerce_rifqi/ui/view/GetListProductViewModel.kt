package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ecommerce_rifqi.di.Injection
import com.example.ecommerce_rifqi.helper.Event
import com.example.ecommerce_rifqi.model.*
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import com.example.ecommerce_rifqi.paging.ProductRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetListProductViewModel(private val productRepository: ProductRepository): ViewModel(){

//    val _query = MutableStateFlow("")
//    private val query: StateFlow<String> = _query

    val _query = MutableLiveData<String>()
    private val query: LiveData<String> = _query

    val search = query.switchMap {
        productRepository.getProduct(
            it
        ).cachedIn(viewModelScope)
    }

    fun productListPaging(search: String?): LiveData<PagingData<DataProduct>> =
        productRepository.getProduct(search).cachedIn(viewModelScope)

}

class ViewModelFactoryProduct(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetListProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetListProductViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}