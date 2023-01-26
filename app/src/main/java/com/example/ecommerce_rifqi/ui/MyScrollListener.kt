package com.example.ecommerce_rifqi.ui

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.lang.Runnable

class MyScrollListener(private val fab: ExtendedFloatingActionButton) : RecyclerView.OnScrollListener() {

    private var isVisible = true
    private var scrollDirection = 0

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy != 0) {
            scrollDirection = if (dy > 0) 1 else -1
            fab.hide()
            isVisible = false

        } else{
            searchJob = coroutineScope.launch{
                delay(2000)
                fab.show()
                isVisible = true
            }
        }
    }
}