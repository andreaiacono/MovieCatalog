package org.andreaiacono.moviecatalog.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import jcifs.smb.SmbFile
import java.io.InputStream
import java.io.Serializable


class NasService(val url: String) : Serializable {

    val LOG_TAG = this.javaClass.name

    fun getMoviesDirectories(): Array<SmbFile> {
        val moviesRoot = SmbFile(url)
        return moviesRoot.listFiles()
    }

    private fun getFullImage(dirName: String, filename: String): Bitmap {
        val fullName = "$url$dirName$filename"
        Log.d(LOG_TAG, "Loading image $fullName")
        return urlImageToBitmap(SmbFile(fullName).inputStream)
    }

    fun getThumbnail(dirName: String): Bitmap = getFullImage(dirName, "folder.jpg")

    fun getFullImage(dirName: String): Bitmap = getFullImage(dirName, "about.jpg")

    private fun urlImageToBitmap(imageStream: InputStream): Bitmap = BitmapFactory.decodeStream(imageStream)
}

