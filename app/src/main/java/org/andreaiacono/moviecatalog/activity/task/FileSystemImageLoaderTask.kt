package org.andreaiacono.moviecatalog.activity.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener

internal class FileSystemImageLoaderTask(taskListener: PostTaskListener<Any>, val moviesCatalog: MoviesCatalog, val progressBar: ProgressBar) : AsyncTask<String, Integer, Void>() {

    private val syncTaskType: AsyncTaskType = AsyncTaskType.FILE_SYSTEM_IMAGE_LOAD

    val LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var bitmaps: MutableList<Bitmap> = mutableListOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8))
    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.max = moviesCatalog.getCount()
        progressBar.bringToFront()
        progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg dirName: String): Void? {
        moviesCatalog.movies.forEachIndexed {index, movie ->
            try {
                val filename = "${moviesCatalog.context.filesDir}/${movie.thumbName}"
                Log.d(LOG_TAG, "Loading image $filename")
                bitmaps.add(BitmapFactory.decodeFile(filename))
                publishProgress(index as Integer)
            }
            catch (e: Exception) {
                this.exception = e
                Log.e(LOG_TAG, "Error while loading image $dirName: ${e.message}")
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Integer?) {
        super.onProgressUpdate(values[0])
        progressBar.progress = values[0]!!.toInt()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(ArrayList<Bitmap>(bitmaps), syncTaskType, exception)
        progressBar.visibility = View.GONE
    }
}