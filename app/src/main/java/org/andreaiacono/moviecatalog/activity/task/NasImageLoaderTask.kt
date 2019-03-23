package org.andreaiacono.moviecatalog.activity.task

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener

internal class NasImageLoaderTask(taskListener: PostTaskListener<Any>, val nasService: NasService, val movieDir: String) :
    AsyncTask<String, Int, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_IMAGE_LOAD

    private var LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var postTaskListener: PostTaskListener<Any> = taskListener
    private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)

    override fun doInBackground(vararg url: String): Void? {
        Log.d(LOG_TAG, "Loading image from NAS: $movieDir")
        try {
            bitmap = nasService.getFullImage(movieDir)
        }
        catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(bitmap, asyncTaskType, exception)
    }
}