package com.example.kotlinreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinreport.common.recyclerview.listener.OnScrollLoadMoreRecyclerViewListener
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mUsers: ArrayList<User?>
    private lateinit var mUserAdapter: UserAdapter
    private lateinit var mOnLoadMore: OnScrollLoadMoreRecyclerViewListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRefresh()
        initRecyclerView()
    }

    private fun initRefresh() {
        swipeRefreshLayoutMain.setOnRefreshListener(this)
    }
    private fun initRecyclerView() {
        mUsers = fakeData()
        mUserAdapter = UserAdapter(mUsers)
        mOnLoadMore = object : OnScrollLoadMoreRecyclerViewListener(15) {
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

        Thread.sleep(2000)
        mUserAdapter.closeLoading()
        mUserAdapter.addAll(fakeData())
        mOnLoadMore.mIsLoading = false
        mOnLoadMore.mHasNextPage = true

    }

    private fun fakeData(): ArrayList<User?> {
        var list: ArrayList<User?> = ArrayList()
        var i: Int = 0
        while (i < 15) {
            list.add(User(i, "A${i}", SexType.MALE, null))
            i++
        }
        return list
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        mUserAdapter.clear()
        mUserAdapter.addAll(fakeData())
        swipeRefreshLayoutMain.isRefreshing = false
    }
}
