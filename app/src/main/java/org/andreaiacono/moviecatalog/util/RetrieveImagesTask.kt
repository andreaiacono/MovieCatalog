package org.andreaiacono.moviecatalog.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL
import org.andreaiacono.moviecatalog.PostTaskListener
import java.util.logging.Level
import java.util.logging.Logger


internal class RetrieveImagesTask(taskListener: PostTaskListener<List<Bitmap>>) :
    AsyncTask<String, Void, Void>() {

    private var logger: Logger = Logger.getAnonymousLogger()
    private var exception: Exception? = null
    private var bitmapList: MutableList<Bitmap> = mutableListOf()

    private var postTaskListener: PostTaskListener<List<Bitmap>> = taskListener

    override fun doInBackground(vararg urls: String): Void? {
        try {
            try {
                val image = urlImageToBitmap("https://placehold.it/200x300")
                for (i in 0..50) {
                    bitmapList.add(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(bitmapList)
    }

    @Throws(Exception::class)
    private fun urlImageToBitmap(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        return BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

}