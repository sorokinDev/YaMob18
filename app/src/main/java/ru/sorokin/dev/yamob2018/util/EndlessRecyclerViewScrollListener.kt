package ru.sorokin.dev.yamob2018.util

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView


abstract class EndlessRecyclerViewScrollListener(val layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {
    var visibleThreshold = 100
    var previousTotalItemCount = 0
    var loading = mutableLiveDataWithValue(false)


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

        /*if (totalItemCount < previousTotalItemCount) {
            this.currentOffset = this.startingOffset
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }*/

        if (!loading.value!! && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            if(previousTotalItemCount != totalItemCount){
                beforeLoadMore(0, totalItemCount, view)
            }
        }

    }


    // Call this method whenever performing new searches

    fun resetState() {
        this.previousTotalItemCount = 0
        this.loading.value = false
    }

    fun beforeLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?){
        loading.value = true
        previousTotalItemCount = totalItemsCount

        loadMore(page, totalItemsCount, view)
    }

    fun afterLoadMore(){
        loading.value = false
    }


    abstract fun loadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)


}