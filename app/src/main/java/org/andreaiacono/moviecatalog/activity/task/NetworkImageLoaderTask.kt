package org.andreaiacono.moviecatalog.activity.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import java.net.URL
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import java.util.logging.Logger


internal class NetworkImageLoaderTask(taskListener: PostTaskListener<Any>) : AsyncTask<String, Void, Void>() {

    private val syncTaskType: AsyncTaskType = AsyncTaskType.INTERNET_IMAGE_LOAD

    private var logger: Logger = Logger.getAnonymousLogger()
    private lateinit var exception: Exception
    private lateinit var bitmap: Bitmap

    private var postTaskListener: PostTaskListener<Any> = taskListener

    override fun doInBackground(vararg url: String): Void? {
        logger.fine("Loading image at $url")
        try {
            bitmap = urlImageToBitmap(url[0])
        }
        catch (e: Exception) {
            this.exception = e
            logger.severe("Error while loading image $url: ${e.message}")
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(bitmap, syncTaskType, exception)
    }

    @Throws(Exception::class)
    private fun urlImageToBitmap(imageUrl: String): Bitmap =BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
}