package com.example.kotlinreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinreport.adapter.UserAdapter
import com.example.kotlinreport.common.paginator.Paginator
import com.example.kotlinreport.common.recyclerview.listener.OnScrollLoadMoreRecyclerViewListener
import com.example.kotlinreport.config.AppConfig
import com.example.kotlinreport.db.DatabaseManager
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import com.example.kotlinreport.view.create.CreateActivity
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    SwipeRefreshLayout.OnRefreshListener,
    NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mUsers: ArrayList<User>
    private lateinit var mUserAdapter: UserAdapter
    private lateinit var mOnLoadMore: OnScrollLoadMoreRecyclerViewListener
    private lateinit var mPaginator: Paginator
    private lateinit var mRecyclerViewUser: RecyclerView
    private lateinit var mSwipeRefreshLayoutMain: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        initPaginatorUser()

        initRefresh()
        initRecyclerView()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == R.id.nav_create) {
            var intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        } else if (itemId == R.id.nav_fake_data) {
            Toast.makeText(this, "Fake data add 10 users", Toast.LENGTH_SHORT).show()
            fakeData()
        } else if (itemId == R.id.nav_clear_data) {
            DatabaseManager.deleteAllUser(this);
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawers()
        return true
    }

    private fun initPaginatorUser() {
        val total: Long = DatabaseManager.countUser(this)
        Log.d("total", total.toString())
        mPaginator = Paginator(1, AppConfig.Pagination.PAGE_SIZE, total)
    }

    private fun initRefresh() {
        mSwipeRefreshLayoutMain = findViewById(R.id.swipeRefreshLayoutMain)
        mSwipeRefreshLayoutMain.setOnRefreshListener(this)
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
        mRecyclerViewUser = findViewById(R.id.recyclerViewMainUser)
        mRecyclerViewUser.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 1)
            setHasFixedSize(true)
            adapter = mUserAdapter
            addOnScrollListener(mOnLoadMore)
        }
    }

    fun getData() {
        // accept load more if not refresh
        if (!mSwipeRefreshLayoutMain.isRefreshing) {
            mOnLoadMore.mIsLoading = true
            mUserAdapter.openLoading()
        }

        var hander = Handler()
        hander.postDelayed(Runnable() {
            if (!mSwipeRefreshLayoutMain.isRefreshing) {
                mUserAdapter.closeLoading()
            }
            val users = DatabaseManager.getUserList(this, mPaginator.getOffset(), mPaginator.mPageSize)
            Log.d("addAllUser", users.size.toString())
            mUserAdapter.addAll(users)

            if (mSwipeRefreshLayoutMain.isRefreshing) {
                mSwipeRefreshLayoutMain.isRefreshing = false
            }

            mOnLoadMore.mIsLoading = false
            if (mPaginator.hasNextPage()) {
                mOnLoadMore.mHasNextPage = true
                mPaginator.mPage++
            } else {
                mOnLoadMore.mHasNextPage = false
            }
        }, 2000)


    }

    private fun fakeData(): Unit {
        val total: Long = DatabaseManager.countUser(this)
        var i: Long = 1
        while (i < 11) {
            var sex: SexType = SexType.MALE
            if ((i % 2).equals(0)) {
                Log.d("sex", "${i}-fe")
                sex = SexType.FEMALE
            }
            DatabaseManager.insertUser( this,
                User(null, "Test ${total + i}", sex, null)
            )
            i++
        }
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        mUserAdapter.clear()
        initPaginatorUser()
        Log.d("onRefreshPaginator", "page:${mPaginator.mPage}|total:${mPaginator.mTotal}|offset:${mPaginator.getOffset()}")
        getData()
    }
}
