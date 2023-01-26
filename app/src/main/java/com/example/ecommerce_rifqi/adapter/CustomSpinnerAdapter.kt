package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.ecommerce_rifqi.R

class CustomSpinnerAdapter(internal var context: Context, internal var images: IntArray, internal var languages: Array<String>):  BaseAdapter() {

    private var inflter: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflter.inflate(R.layout.spinner_custom_layout,null)
        val icon = view.findViewById<View>(R.id.imageView) as ImageView?
        val names = view.findViewById<View>(R.id.textView) as TextView?
        icon!!.setImageResource(images[i])
        names!!.text = languages[i]
        view.setPadding(0, view.paddingTop, 0, view.paddingBottom)
        return view
    }
}