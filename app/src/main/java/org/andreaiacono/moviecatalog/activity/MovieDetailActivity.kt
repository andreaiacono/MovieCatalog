package org.andreaiacono.moviecatalog.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.activity.task.NasImageLoaderTask
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener

class MovieDetailActivity : PostTaskListener<Any>, AppCompatActivity() {

    val LOG_TAG = this.javaClass.name

    override fun onPostTask(result: Any, asyncTaskType: AsyncTaskType, exception: Exception?) {

        if (exception != null) {
            Log.d(LOG_TAG, exception.message, exception)
            val toast = Toast.makeText(
                applicationContext,
                "An error occurred while executing $asyncTaskType: ${exception.message}",
                Toast.LENGTH_LONG
            )
            toast.show()
        }
        else {
            imageView.setImageBitmap(result as Bitmap)
        }
    }

    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail)

        val toolbar: Toolbar = findViewById(R.id.movieDetailToolbar)
        setSupportActionBar(toolbar)

        imageView = findViewById(R.id.movie_fullscreen_view)

        val movieDir = intent.extras.get("position") as String
        val nasService =  intent.extras.get("NasService") as NasService
        NasImageLoaderTask(this, nasService, movieDir).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_trailer -> {
                true
            }
            R.id.action_play -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
