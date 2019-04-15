package org.andreaiacono.moviecatalog.task

import android.os.AsyncTask
import android.util.Log
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.NasService

internal class NasMoviesUpdaterTask(taskListener: PostTaskListener<Any>, val nasService: NasService, val movies: List<Movie>) :
    AsyncTask<String, Int, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_MOVIE_UPDATE

    private var LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg url: String): Void? {
        try {
            movies.forEach {
                var xml = nasService.loadXml(it.nasDirName)
                if (!xml.contains("<seen>")) {
                    Log.d(LOG_TAG, "Adding seen tag")
                    xml = xml.replace("</movie>", "<seen>true</seen>\n</movie>")
                }
                else {
                    Log.d(LOG_TAG, "Updating seen tag")
                    if (xml.contains("<seen>false</seen>")) {
                        xml = xml.replace("<seen>false</seen>", "<seen>true</seen>")
                    }
                    else {
                        xml = xml.replace("<seen>true</seen>", "<seen>false</seen>")
                    }
                }
                nasService.saveXml(it.nasDirName, xml)
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

