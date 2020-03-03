package com.example.kotlinreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinreport.common.paginator.Paginator
import com.example.kotlinreport.common.recyclerview.listener.OnScrollLoadMoreRecyclerViewListener
import com.example.kotlinreport.config.AppConfig
import com.example.kotlinreport.db.DatabaseManager
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mUsers: ArrayList<User>
    private lateinit var mUserAdapter: UserAdapter
    private lateinit var mOnLoadMore: OnScrollLoadMoreRecyclerViewListener
    private lateinit var mPaginator: Paginator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPaginatorUser()

        initRefresh()
        initRecyclerView()
    }

    private fun initPaginatorUser() {
        val total: Long = DatabaseManager.countUser(this)
        Log.d("total", total.toString())
        mPaginator = Paginator(1, AppConfig.Pagination.PAGE_SIZE, total)
    }

    private fun initRefresh() {
        swipeRefreshLayoutMain.setOnRefreshListener(this)
    }
    private fun initRecyclerView() {
        mUsers = DatabaseManager.getUserList(this, mPaginator.getOffset(), mPaginator.mPageSize)
        Log.d("Users", mUsers.size.toString())
        if (mPaginator.hasNextPage()) {
            mPaginator.mPage++
        }

        mUserAdapter = UserAdapter(mUsers as ArrayList<User?>)
        mOnLoadMore = object : OnScrollLoadMoreRecyclerViewListener(10) {
            override fun onLoadMore() {
                Toast.makeText(this@MainActivity, "onLoadMore", Toast.LENGTH_SHORT).show()
                getData()
            }
        }
        recyclerViewMainUser.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 1)
            setHasFixedSize(true)
            adapter = mUserAdapter
            addOnScrollListener(mOnLoadMore)
        }
    }

    fun getData() {
        mOnLoadMore.mIsLoading = true
        mUserAdapter.openLoading()

        var hander = Handler()
        hander.postDelayed(Runnable() {
            mUserAdapter.closeLoading()
            val users = DatabaseManager.getUserList(this, mPaginator.getOffset(), mPaginator.mPageSize)
            Log.d("addAllUser", users.size.toString())
            mUserAdapter.addAll(users)

            mOnLoadMore.mIsLoading = false
            if (mPaginator.hasNextPage()) {
                mOnLoadMore.mHasNextPage = true
                mPaginator.mPage++
            } else {
                mOnLoadMore.mHasNextPage = false
            }
        }, 2000)


    }

    private fun fakeData(): ArrayList<User?> {

        var list: ArrayList<User?> = ArrayList()
        var i: Long = 0
        while (i < 10) {
            list.add(User(i, "A${i}", SexType.MALE, null))
            i++
        }
        return list
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        mUserAdapter.clear()
        initPaginatorUser()
        Log.d("onRefreshPaginator", "page:${mPaginator.mPage}|total:${mPaginator.mTotal}|offset:${mPaginator.getOffset()}")
        getData()
        swipeRefreshLayoutMain.isRefreshing = false
    }
}
