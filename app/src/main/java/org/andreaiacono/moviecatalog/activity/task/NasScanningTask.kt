package org.andreaiacono.moviecatalog.activity.task

import android.os.AsyncTask
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.ProgressBar
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.util.thumbNameNormalizer

internal class NasScanningTask(taskListener: PostTaskListener<Any>, val moviesCatalog: MoviesCatalog, val horizontalProgressBar: ProgressBar, val indefiniteProgressBar: ProgressBar) :
    AsyncTask<String, Integer, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_SCAN

    private var LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var movies: List<NasMovie> = listOf()
    private var postTaskListener: PostTaskListener<Any> = taskListener
    lateinit var result: Pair<List<NasMovie>, List<String>>
    var isReady: Boolean = false

    override fun onPreExecute() {
        super.onPreExecute()
        indefiniteProgressBar.visibility = View.VISIBLE
        indefiniteProgressBar.isIndeterminate = true
        object : Thread() {
            override fun run() {
                try {
                    result = moviesCatalog.nasService.getTitles(moviesCatalog.movies)
                    horizontalProgressBar.max = result.first.size
                    indefiniteProgressBar.visibility = View.GONE
                }
                catch (ex:Exception) {
                    exception = ex
                    indefiniteProgressBar.visibility = View.GONE
                }
                isReady = true
            }
        }.start()

        while (!isReady) {
            Thread.sleep(100)
        }
        horizontalProgressBar.bringToFront()
        horizontalProgressBar.progress = 0
        horizontalProgressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg url: String): Void? {
        if (exception != null) {
            // if there was an error in onPreExecute(), just stops here
            return null
        }
        Log.d(LOG_TAG, "Loading XML data from NAS")
        try {
            if (!result.second.isEmpty()) {
                e(this.LOG_TAG, ("Not processed movies: ${result.second}"))
            }
            movies = result.first
            Log.d(LOG_TAG, "NAS new data: $movies")
            movies.forEachIndexed { index, movie ->
                Log.d(LOG_TAG, "Scanning $movie")
                publishProgress(index as Integer)
                try {
                    val thumbFilename = thumbNameNormalizer(movie.title)
                    Log.d(LOG_TAG, "Saving $thumbFilename (dirnmae=${movie.dirName})")
                    moviesCatalog.saveBitmap(thumbFilename, moviesCatalog.nasService.getThumbnail(movie.dirName))
                } catch (ex: Exception) {
                    Log.e(LOG_TAG, ex.message, ex)
                }
            }
        } catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Integer?) {
        super.onProgressUpdate(values[0])
        horizontalProgressBar.progress = values[0]!!.toInt()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(movies, asyncTaskType, exception)
        horizontalProgressBar.visibility = View.GONE
    }
}