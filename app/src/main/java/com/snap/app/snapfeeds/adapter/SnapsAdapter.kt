package com.snap.app.snapfeeds.adapter

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.snap.app.R
import com.snap.app.SnapApp
import com.snap.app.SnapApp.Companion.firebaseAuth
import com.snap.app.snapfeeds.model.Snap
import com.snap.app.snapfeeds.ui.PostImageDescActivity
import com.snap.app.snapfeeds.view_model.FeedViewModel
import com.snap.app.snapfeeds.view_model.PostImageViewModel
import com.snap.app.util.Constants

class SnapsAdapter(var snapsList: MutableList<Snap>, private val deleteSnap: (Snap) -> Unit) : RecyclerView.Adapter<SnapsAdapter.ViewHolder>() {

    init {
        snapsList.sortByDescending { it.timestamp }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snap = snapsList.get(position) ?: null
        with(holder) {
            txtDescription.setText(snap?.description)
            com.squareup.picasso.Picasso.get().load(snap?.url).into(imageView)
            txtName.setText(snap?.name)
            imageViewEdit.setOnClickListener {
                val intent = Intent(imageView.context, PostImageDescActivity::class.java)
                intent.putExtra(Constants.SNAP, snap)
                imageViewEdit.context.startActivity(intent)
            }
            imageViewDelete.setOnClickListener({
                snap?.let { deleteSnap(it) }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_snap_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return snapsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val imageViewEdit: ImageView = itemView.findViewById(R.id.edit)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.delete)
        val txtDescription: TextView = itemView.findViewById(R.id.description)
        val txtName: TextView = itemView.findViewById(R.id.name)
    }


}


