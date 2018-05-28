package com.snap.app.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore


object Utility {

    fun getFileExtension(uri: Uri): String {
        val filePath = uri.path
        return filePath.substring(filePath.lastIndexOf("."))
    }

    fun getPath(context: Context, uri: Uri): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.getContentResolver().query(uri, proj, null, null, null) ?: null
        cursor?.moveToFirst()?.let {
            val column_index = cursor.getColumnIndexOrThrow(proj[0])
            result = cursor.getString(column_index)
            cursor.close()
        }
        return result
    }
}