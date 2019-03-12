package org.andreaiacono.moviecatalog.activity

import android.content.Intent
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
import org.andreaiacono.moviecatalog.activity.task.DuneHdCommanderTask
import org.andreaiacono.moviecatalog.activity.task.NasImageLoaderTask
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.DuneHdService
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.ui.PostTaskListener

class MovieDetailActivity : PostTaskListener<Any>, AppCompatActivity() {

    val LOG_TAG = this.javaClass.name

    lateinit var movie: Movie

    override fun onPostTask(result: Any, asyncTaskType: AsyncTaskType, exception: Exception?) {

        if (exception != null) {
            Log.d(LOG_TAG, exception.message, exception)
            val toast = Toast.makeText(applicationContext, "An error occurred while executing $asyncTaskType: ${exception.message}", Toast.LENGTH_LONG)
            toast.show()
        }
        else {
            when (asyncTaskType) {
                AsyncTaskType.NAS_IMAGE_LOAD -> {
                    imageView.setImageBitmap(result as Bitmap)
                }
                AsyncTaskType.DUNE_HD_COMMANDER -> {
                    val toast = Toast.makeText(applicationContext, result as String, Toast.LENGTH_LONG)
                    toast.show()
                }}
        }
    }

    lateinit var imageView: ImageView
    lateinit var duneHdService: DuneHdService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail)

        val toolbar: Toolbar = findViewById(R.id.movieDetailToolbar)
        setSupportActionBar(toolbar)

        imageView = findViewById(R.id.movie_fullscreen_view)

        movie = intent.extras.get("movie") as Movie
        val nasService =  intent.extras.get("NasService") as NasService
        duneHdService =  intent.extras.get("DuneHdService") as DuneHdService
        NasImageLoaderTask(this, nasService, movie.dirName).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movie_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_trailer -> {
                if (movie != null) {
                    val intent = Intent(Intent.ACTION_SEARCH)
                    intent.setPackage("com.google.android.youtube")
                    intent.putExtra("query", movie.title + " trailer ita")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                true
            }
            R.id.action_play -> {
                DuneHdCommanderTask(this, duneHdService).execute(movie.dirName, movie.videoFileName)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
