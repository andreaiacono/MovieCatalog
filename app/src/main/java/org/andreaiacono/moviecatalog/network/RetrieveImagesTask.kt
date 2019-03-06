package org.andreaiacono.moviecatalog.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL
import org.andreaiacono.moviecatalog.util.PostTaskListener
import java.util.logging.Logger


internal class NetworkImageLoader(taskListener: PostTaskListener<Bitmap>) : AsyncTask<String, Void, Void>() {

    private var logger: Logger = Logger.getAnonymousLogger()
    private lateinit var exception: Exception
    private lateinit var bitmap: Bitmap

    private var postTaskListener: PostTaskListener<Bitmap> = taskListener

    override fun doInBackground(vararg url: String): Void? {
        logger.fine("Loading image at $url")
        try {
            bitmap = urlImageToBitmap(url[0])
        }
        catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(bitmap, exception)

    }

    @Throws(Exception::class)
    private fun urlImageToBitmap(imageUrl: String): Bitmap =BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
}