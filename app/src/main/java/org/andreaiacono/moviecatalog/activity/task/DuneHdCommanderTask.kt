package org.andreaiacono.moviecatalog.activity.task

import android.os.AsyncTask
import android.util.Log
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import java.util.*

internal class DuneHdCommanderTask(taskListener: PostTaskListener<Any>, val moviesCatalog: MoviesCatalog) :
    AsyncTask<String, Void, Void>() {

    private val syncTaskType: AsyncTaskType = AsyncTaskType.DUNE_HD_COMMANDER

    val LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var result = ""

    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg names: String): Void? {
        Log.d(LOG_TAG, "names: ${Arrays.toString(names)}")
        val response = moviesCatalog.duneHdService.startMovie(names[0], names[1])
        Log.i(LOG_TAG, "Dune call returned $response")
        // if response is false, throws exception
        result = "Movie ${names[0]} started"
        return null
    }

    override fun onPostExecute(postResult: Void?) {
        super.onPostExecute(postResult)
        postTaskListener.onPostTask(result, syncTaskType, exception)
    }
}