package org.andreaiacono.moviecatalog.activity.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import java.net.URL
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import java.util.logging.Logger


internal class FileSystemImageLoaderTask(taskListener: PostTaskListener<Any>, val moviesCatalog: MoviesCatalog) :
    AsyncTask<String, Void, Void>() {

    private val syncTaskType: AsyncTaskType = AsyncTaskType.FILE_SYSTEM_IMAGE_LOAD

    val LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var bitmaps: MutableList<Bitmap> = mutableListOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8))

    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg dirName: String): Void? {
        Log.d(LOG_TAG, "Started loading images form disk")
        moviesCatalog.movies.forEach {
            try {
                val filename = "${moviesCatalog.main.application.applicationContext.filesDir}/${it.thumbName}"
                Log.d(LOG_TAG, "Loading image $filename")
                bitmaps.add(BitmapFactory.decodeFile(filename))
            }
            catch (e: Exception) {
                this.exception = e
                Log.e(LOG_TAG, "Error while loading image $dirName: ${e.message}")
            }
        }
        Log.d(LOG_TAG, "Finished loading images form disk")
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(bitmaps, syncTaskType, exception)
    }
}