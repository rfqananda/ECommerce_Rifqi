package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.model.ImageProduct

class ImagePagerAdapter(private val images: List<ImageProduct>) : PagerAdapter() {

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(container.context)
        val view = layoutInflater.inflate(R.layout.view_pager_layout, container, false)
        val imageView = view.findViewById<ImageView>(R.id.iv_detail_product)
        val titleView = view.findViewById<TextView>(R.id.tv_detail_image)
        Glide.with(container.context)
            .load(images[position].image_product)
            .centerCrop()
            .into(imageView)
        titleView.text = images[position].title_product
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}