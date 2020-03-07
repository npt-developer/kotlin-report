package com.example.kotlinreport.common.paginator

import android.util.Log
import com.example.kotlinreport.config.AppConfig

class Paginator {
    var mPage: Long = 1
    var mPageSize: Long = AppConfig.Pagination.PAGE_SIZE
    var mTotal: Long

    constructor(page: Long, pageSize: Long, total: Long) {
        this.mPage = page
        this.mPageSize = pageSize
        this.mTotal = total
    }

    fun getOffset(): Long {
        val offset: Long = (mPage - 1) * mPageSize
        return offset
    }

    fun hasNextPage(): Boolean {
        val pageLasted: Double = Math.ceil((mTotal.toDouble() / this.mPageSize.toDouble()))
        return this.mPage < pageLasted
    }

    fun isPageExists(): Boolean {
        val pageLasted: Double = Math.ceil((mTotal / this.mPageSize).toDouble())
        return this.mPage <= pageLasted
    }

    fun isPageExists(page: Long): Boolean {
        val pageLasted: Double = Math.ceil((mTotal / this.mPageSize).toDouble())
        return page <= pageLasted
    }

}
