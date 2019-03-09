package org.andreaiacono.moviecatalog.activity.task

import android.os.AsyncTask
import android.util.Log
import android.util.Log.e
import android.widget.ProgressBar
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.util.thumbNameNormalizer

internal class NasScanningTask(
    taskListener: PostTaskListener<Any>,
    val moviesCatalog: MoviesCatalog,
    nasProgressBar: ProgressBar
) : AsyncTask<String, Void, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_SCAN

    private var LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var movies: List<NasMovie> = listOf()
    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg url: String): Void? {
        Log.d(LOG_TAG,"Loading data from NAS")
        try {
            val pair = moviesCatalog.nasService.getTitles(moviesCatalog.movies)
            if (!pair.second.isEmpty()) {
                e(this.LOG_TAG,("Not processed movies: ${pair.second}"))
            }
            movies = pair.first
            Log.d(LOG_TAG,"NAS new data: $movies")
            movies.forEach {
                Log.d(LOG_TAG,"Scanning $it")
                try {
                    val thumbFilename = thumbNameNormalizer(it.title)
                    Log.d(LOG_TAG,"saving $thumbFilename (dirnmae=${it.dirName})")
                    moviesCatalog.saveBitmap(thumbFilename, moviesCatalog.nasService.getThumbnail(it.dirName))
                }
                catch (ex: Exception) {
                    Log.e(LOG_TAG, ex.message, ex)
                }
            }
        }
        catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(movies, asyncTaskType, exception)
    }
}