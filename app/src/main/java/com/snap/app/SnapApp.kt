package com.snap.app

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SnapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        firebaseAuth = FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference
        mDB = FirebaseDatabase.getInstance()

    }

    companion object {
        lateinit var instance: SnapApp
            private set
        lateinit var firebaseAuth: FirebaseAuth
            private set
        lateinit var mStorageRef: StorageReference
            private set
        lateinit var mDB: FirebaseDatabase
            private set


    }
}