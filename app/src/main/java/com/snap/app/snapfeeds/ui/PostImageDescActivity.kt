package com.snap.app.snapfeeds.ui

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.snap.app.R
import com.snap.app.snapfeeds.view_model.PostImageViewModel
import com.snap.app.util.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_image.*
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import com.snap.app.snapfeeds.model.Snap
import java.io.Serializable


class PostImageDescActivity : AppCompatActivity() {

    private lateinit var postImageViewModel: PostImageViewModel
    private var filePath: String? = null
    private var isEditing = false
    private var snap: Snap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_image)
        postImageViewModel = ViewModelProviders.of(this).get(PostImageViewModel::class.java)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.getStringExtra(Constants.EXTRA_FILEPATH)?.let {
            filePath = intent.getStringExtra(Constants.EXTRA_FILEPATH)
            Picasso.get().load(filePath).into(imageView)
        }

        intent.getSerializableExtra(Constants.SNAP)?.let {
            snap = intent?.getSerializableExtra(Constants.SNAP) as Snap
            isEditing = true
            filePath = snap?.url
            Picasso.get().load(snap?.url).into(imageView)
            snap?.description?.let { editText.setText(snap?.description) }

        }

        textView.setOnClickListener {
            if (isEditing) {
                snap?.description = editText.text.toString()
                postImageViewModel.editPost(snap)
            } else {
                val myUri = Uri.parse(filePath)
                postImageViewModel.uploadBitmap(myUri, editText.text.toString())
            }
        }

        postImageViewModel.bitmapUploadResult.observe(this, Observer {
            when (it) {
                is PostImageViewModel.BitmapUploadResult.Success -> {
                    val intent = Intent(this@PostImageDescActivity, FeedsActivity::class.java)
                    startActivity(intent)
                    showProgressOrHide(false, null)
                }
                is PostImageViewModel.BitmapUploadResult.Error -> {
                    showProgressOrHide(false, null)
                }
                is PostImageViewModel.BitmapUploadResult.ShowLoading -> {
                    showProgressOrHide(true, it.message)
                }
            }
        })
    }

    private fun showProgressOrHide(b: Boolean, message: String?) {
        val progressDialog = ProgressDialog(this)
        if (b) {
            progressDialog.setMessage(message)
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}