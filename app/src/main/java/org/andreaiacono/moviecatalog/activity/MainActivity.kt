package org.andreaiacono.moviecatalog.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import android.widget.Toast
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.activity.task.MovieLoaderTask
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import java.util.logging.Logger
import org.andreaiacono.moviecatalog.util.ImageAdapter
import org.andreaiacono.moviecatalog.ui.PostTaskListener


class MainActivity : PostTaskListener<Any>, AppCompatActivity() {

    private var logger: Logger = Logger.getAnonymousLogger()

    private lateinit var imageGrid: GridView
    lateinit var moviesCatalog: MoviesCatalog

    override fun onPostTask(result: Any, asyncTaskType: AsyncTaskType, exception: Exception?) {

        if (exception != null) {
            val toast = Toast.makeText(applicationContext, "An error occurred: ${exception.message}", Toast.LENGTH_LONG)
            toast.show()
        } else
            when (asyncTaskType) {
                AsyncTaskType.INTERNET_IMAGE_LOAD -> {
                    this.imageGrid.setAdapter(ImageAdapter(this, result as List<Bitmap>))
                }
                AsyncTaskType.NAS_SCAN -> {

                }
                AsyncTaskType.DEVICE_IMAGE_LOAD -> {

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        Logger.getAnonymousLogger().fine("Main onCreate")
        moviesCatalog = MoviesCatalog(this.application.applicationContext, "smb://192.168.1.90/Volume_1/movies/")
//        setSupportActionBar(toolbar)
//        this.imageGrid = findViewById(R.id.gridview)
//        NetworkImageLoaderTask(this).execute()
        val myToolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(myToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_scan -> {
                MovieLoaderTask(this, NasService("smb://192.168.1.90/Volume_1/movies/")).execute()
                true
            }
            R.id.action_info -> {
                val builder = AlertDialog.Builder(this)
                val ip = "192.168.1.0"
                val message = "Ip Address Dune HD: $ip" +
                "\nAPI version: 5" +
                "\nMovies number: " + moviesCatalog.getCount()
                builder.setMessage(message).setTitle(R.string.info_title)
                val dialog = builder.create()
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
