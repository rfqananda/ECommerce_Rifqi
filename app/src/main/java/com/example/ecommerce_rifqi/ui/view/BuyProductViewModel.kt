package com.example.ecommerce_rifqi.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BuyProductViewModel : ViewModel(){

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private var initTotalPrice: Int? = 0

    init {
        _quantity.value = 1
    }

    fun increaseQuantity(stock: Int?) {
        if (_quantity.value!! < stock!!) {
            _quantity.value = _quantity.value?.plus(1)
            _totalPrice.value = initTotalPrice?.times(_quantity.value!!.toInt())
        }
    }


    fun decreaseQuantity() {
        if (quantity.value == 1) {
            _quantity.value = 1
            _totalPrice.value = initTotalPrice!!.toInt()
        } else {
            _quantity.value = _quantity.value?.minus(1)
            _totalPrice.value = initTotalPrice?.times(_quantity.value!!.toInt())
        }
    }

    fun setPrice(productPrice: Int) {
        initTotalPrice = productPrice
        _totalPrice.value = productPrice
    }





}