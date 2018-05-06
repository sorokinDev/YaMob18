package ru.sorokin.dev.yamob2018.util

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView


abstract class EndlessRecyclerViewScrollListener(val layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {
    var visibleThreshold = 50
    var previousTotalItemCount = 0
    var loadedLastTime = 1

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }

        return maxSize
    }

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


    // Call this method whenever performing new searches

    fun resetState() {
        this.previousTotalItemCount = 0

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