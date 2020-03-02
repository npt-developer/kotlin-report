package com.example.kotlinreport.common.recyclerview.listener

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class OnScrollLoadMoreRecyclerViewListener: RecyclerView.OnScrollListener {

    var mIsLoading: Boolean = false
    var mHasNextPage: Boolean = true
    private var mPageSize: Int

    constructor(): super() {
        mPageSize = 10
    }

    constructor(pageSize: Int): super() {
        mPageSize = pageSize
    }



    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager: GridLayoutManager = recyclerView.layoutManager as GridLayoutManager
        val visibleItemCount: Int = layoutManager.childCount
        val totalItemCount: Int = layoutManager.itemCount
        val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
        if (!mIsLoading && mHasNextPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
                && totalItemCount >= mPageSize) {
                onLoadMore();
            }
        }
    }

    abstract fun onLoadMore(): Unit

}