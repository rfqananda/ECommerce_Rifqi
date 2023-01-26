package com.example.ecommerce_rifqi.ui

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.ecommerce_rifqi.R

class LoadingDialog(val mActivity: Activity) {

    private lateinit var isLoading: AlertDialog

    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val loadingView = inflater.inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(loadingView)
        builder.setCancelable(false)
        isLoading = builder.create()
        isLoading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isLoading.show()
    }

    fun isDismiss(){
        isLoading.dismiss()
    }
}