package com.snap.app.snapfeeds.view_model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.snap.app.R
import com.snap.app.SnapApp
import com.snap.app.SnapApp.Companion.firebaseAuth
import com.snap.app.SnapApp.Companion.mDB
import com.snap.app.snapfeeds.model.Snap
import java.util.*

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    var snapResult = MutableLiveData<SnapResult>()
    val noSnaps = application.getString(R.string.no_snaps)


    fun loadPictures() {
        snapResult.value = SnapResult.ShowLoading("Loading Your Snaps")
        mDB.reference.child(firebaseAuth.currentUser?.uid).child("snaps").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                snapResult.value = SnapResult.Error(p0?.message)
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val snaps = ArrayList<Snap>()
                var name: String? = null
                var description: String? = null
                p0?.children?.forEach { dataSnapShot ->
                    val url = dataSnapShot.child("url").getValue() as String
                    dataSnapShot.child("name").getValue()?.let { name = dataSnapShot.child("name").getValue() as String }
                    dataSnapShot.child("description").getValue()?.let { description = dataSnapShot.child("description").getValue() as String }
                    var timestamp: Long? = Date().time
                    if (dataSnapShot.hasChild("timestamp")) {
                        timestamp = dataSnapShot.child("timestamp").getValue() as Long
                    }
                    snaps.add(Snap(url, name, description, timestamp))
                }
                if (snaps.size > 0) {
                    snapResult.value = SnapResult.Success(snaps)
                } else {
                    snapResult.value = SnapResult.Error(noSnaps)
                }
            }
        })
    }

    fun deleteSnap(snap: Snap?) {
        val snapsReference = SnapApp.mDB.reference?.child(firebaseAuth.uid)?.child("snaps")
        val snapsNameAndDescriptionRef = snapsReference?.child("" + snap?.url?.hashCode())
        snapsNameAndDescriptionRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                p0?.let {
                    for (deleteSnapShot in p0.children) {
                        deleteSnapShot.ref.removeValue()
                    }
                }
            }
        })
    }

    sealed class SnapResult {
        data class Success(val snaps: MutableList<Snap>) : SnapResult()
        data class Error(val errorMessage: String?) : SnapResult()
        data class ShowLoading(val message: String?) : SnapResult()

    }
}