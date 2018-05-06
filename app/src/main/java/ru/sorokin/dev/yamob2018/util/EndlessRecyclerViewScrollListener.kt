package ru.sorokin.dev.yamob2018.util

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView


abstract class EndlessRecyclerViewScrollListener(val layoutManager: GridLayoutManager, var visibleThreshold: Int) : RecyclerView.OnScrollListener() {
    var previousTotalItemCount = 0
    var loadedLastTime = 1

    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager.itemCount

        lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        if (!isLoading() && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            if(loadedLastTime > 0){
                beforeLoadMore(0, totalItemCount, view)
            }
        }

    }

    fun resetState() {
        this.previousTotalItemCount = 0
        this.loadedLastTime = 1
    }

    fun beforeLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?){
        previousTotalItemCount = totalItemsCount

        loadMore(page, totalItemsCount, view)
    }

    fun afterLoadMore(){

    }


    abstract fun loadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
    abstract fun isLoading(): Boolean


}