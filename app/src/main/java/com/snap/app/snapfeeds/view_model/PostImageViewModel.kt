package com.snap.app.snapfeeds.view_model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.snap.app.SnapApp.Companion.firebaseAuth
import com.snap.app.SnapApp.Companion.mDB
import com.snap.app.SnapApp.Companion.mStorageRef
import com.snap.app.snapfeeds.model.Snap
import com.snap.app.snapfeeds.ui.FeedsActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class PostImageViewModel(application: Application) : AndroidViewModel(application) {
    var bitmapUploadResult = MutableLiveData<BitmapUploadResult>()

    fun uploadBitmap(uri: Uri, description: String) {
        try {
            bitmapUploadResult.value = BitmapUploadResult.ShowLoading("Uploading Image")
            val bitmap = MediaStore.Images.Media.getBitmap(getApplication<Application>().contentResolver, uri)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            val bitmapKey = "" + bitmap.hashCode()
            val storage = mStorageRef.child("images/$bitmapKey.jpg")
            storage.putBytes(bytes)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get a URL to the uploaded content
                        val downloadUrl = taskSnapshot.downloadUrl
                        val snap = Snap(downloadUrl.toString(), File(uri.path).name, description, Date().time)
                        saveToDB(snap)
                        bitmapUploadResult.value = BitmapUploadResult.Success(snap)
                    }
                    .addOnFailureListener {
                        bitmapUploadResult.value = BitmapUploadResult.Error("Something went wrong,Please try again later")
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveToDB(snap: Snap) {
        val key = "" + snap.url?.hashCode()
        val photoRef = mDB.reference?.child(firebaseAuth.uid)?.child("snaps")
        val imageData = photoRef?.child(key)
        imageData?.child("url")?.setValue(snap.url)
        imageData?.child("name")?.setValue(snap.name)
        imageData?.child("description")?.setValue(snap.description)
        imageData?.child("timestamp")?.setValue(snap.timestamp)
    }

    fun editPost(snap: Snap?) {

        val snapsReference = mDB.reference?.child(firebaseAuth.uid)?.child("snaps")
        val snapsNameAndDescriptionRef = snapsReference?.child("" + snap?.url?.hashCode())
        snapsNameAndDescriptionRef?.child("url")?.setValue(snap?.url)
        snapsNameAndDescriptionRef?.child("name")?.setValue(snap?.name)
        snapsNameAndDescriptionRef?.child("description")?.setValue(snap?.description)
        snapsNameAndDescriptionRef?.child("timestamp")?.setValue(snap?.timestamp)
        val intent = Intent(getApplication(), FeedsActivity::class.java)
        getApplication<Application>().startActivity(intent)
    }



    sealed class BitmapUploadResult {
        data class Success(var snap: Snap) : BitmapUploadResult()
        data class Error(var errorMesssage: String) : BitmapUploadResult()
        data class ShowLoading(var message: String) : BitmapUploadResult()

    }
}