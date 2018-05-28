package com.snap.app.snapfeeds.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.snap.app.R
import com.snap.app.SnapApp.Companion.firebaseAuth
import com.snap.app.snapfeeds.adapter.SnapsAdapter
import com.snap.app.snapfeeds.view_model.FeedViewModel
import com.snap.app.util.BaseListActivity
import com.snap.app.util.Constants
import kotlinx.android.synthetic.main.activity_feeds.*
import android.view.MenuItem
import com.snap.app.MainActivity


class FeedsActivity : BaseListActivity() {

    private val PICK_IMAGE_REQUEST = 1
    lateinit var feedViewModel: FeedViewModel
    lateinit var snapsAdapter: SnapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeds)
        initviews(false)
        setSupportActionBar(toolbar)
        firebaseAuth.currentUser?.displayName?.let {
            supportActionBar?.setTitle(firebaseAuth.currentUser?.displayName)
        } ?: supportActionBar?.setTitle(getString(R.string.snap_user))
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
        loadSnaps()

    }

    private fun loadSnaps() {
        feedViewModel.loadPictures()
        feedViewModel.snapResult.observe(this, Observer {
            when (it) {
                is FeedViewModel.SnapResult.Success -> {
                    snapsAdapter = SnapsAdapter(it.snaps, { feedViewModel.deleteSnap(it) })
                    recyclerView.adapter = snapsAdapter
                    showResults()
                }
                is FeedViewModel.SnapResult.Error -> {
                    it?.errorMessage?.let {
                        showError(it)
                    } ?: showError(getString(R.string.snaps_error))
                }
                is FeedViewModel.SnapResult.ShowLoading -> {
                    showLoader()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_feeds, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(this@FeedsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.add_snap -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        super.onRefresh()
        loadSnaps()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val uri = data?.data
            val intent = Intent(this@FeedsActivity, PostImageDescActivity::class.java)
            intent.putExtra(Constants.EXTRA_FILEPATH, uri?.toString())
            startActivity(intent)
        }
    }


}