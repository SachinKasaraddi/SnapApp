package com.snap.app.util

import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import com.snap.app.R


abstract class BaseListActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_home)
    }

    val swipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.include_content)
    }

    val textView by lazy {
        findViewById<TextView>(R.id.base_home_textview)
    }

    val loadingProgressBar by lazy {
        findViewById<ContentLoadingProgressBar>(R.id.contentLoading)
    }

    fun initviews(dividerEnabled: Boolean) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false
        if (dividerEnabled) recyclerView.addItemDecoration(DividerItemDecoration(this,VERTICAL))
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    fun showError(error: String) {
        recyclerView.invisible()
        textView.visible()
        textView.text = error
        loadingProgressBar.hide()
        hidePullToRefresh()
    }

    fun showResults() {
        recyclerView.visible()
        textView.invisible()
        loadingProgressBar.hide()
        hidePullToRefresh()
    }

    fun hidePullToRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    fun showLoader() {
        loadingProgressBar.show()
    }

    fun showPullToRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun onRefresh() {
    }

    companion object {
        private val TAG = "BaseActivity"
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.invisible() {
        this.visibility = View.GONE
    }
}
