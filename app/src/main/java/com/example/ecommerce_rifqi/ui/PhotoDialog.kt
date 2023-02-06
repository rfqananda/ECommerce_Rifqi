package com.example.ecommerce_rifqi.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.R

class PhotoDialog(val mActivity: Activity) {

    fun showPhoto(photoURL: String){

        val view = View.inflate(mActivity, R.layout.photo_layout, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(mActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val ivPhoto = dialog.findViewById<ImageView>(R.id.iv_photo)

        Glide.with(mActivity)
            .load(photoURL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivPhoto!!)
    }
}