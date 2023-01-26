package com.example.ecommerce_rifqi.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.ui.view.*

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(context) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(context) as T
        } else if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)){
            return ChangePasswordViewModel(context) as T
        } else if (modelClass.isAssignableFrom(ChangePhotoViewModel::class.java)){
            return ChangePhotoViewModel(context) as T
        } else if (modelClass.isAssignableFrom(GetListProductViewModel::class.java)){
            return GetListProductViewModel(context) as T
        } else if (modelClass.isAssignableFrom(GetListFavoriteProductViewModel::class.java)){
            return GetListFavoriteProductViewModel(context) as T
        } else if (modelClass.isAssignableFrom(GetDetailProductViewModel::class.java)){
            return GetDetailProductViewModel(context) as T
        } else if (modelClass.isAssignableFrom(AddToFavoriteViewModel::class.java)){
            return AddToFavoriteViewModel(context) as T
        } else if (modelClass.isAssignableFrom(RemoveFromFavoriteViewModel::class.java)){
            return RemoveFromFavoriteViewModel(context) as T
        } else if (modelClass.isAssignableFrom(UpdateStockViewModel::class.java)){
            return UpdateStockViewModel(context) as T
        } else if (modelClass.isAssignableFrom(UpdateRateViewModel::class.java)){
            return UpdateRateViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}