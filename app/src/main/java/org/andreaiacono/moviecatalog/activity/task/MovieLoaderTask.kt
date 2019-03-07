package org.andreaiacono.moviecatalog.activity.task

import android.os.AsyncTask
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import java.util.logging.Logger


internal class MovieLoaderTask(taskListener: PostTaskListener<Any>, val nasService: NasService) : AsyncTask<String, Void, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_SCAN

    private var logger: Logger = Logger.getAnonymousLogger()
    private lateinit var exception: Exception
    private var movies: List<Movie> = listOf()

    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg url: String): Void? {
        logger.fine("Loading data from NAS")
        try {
            movies = nasService.getAllTitles()
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