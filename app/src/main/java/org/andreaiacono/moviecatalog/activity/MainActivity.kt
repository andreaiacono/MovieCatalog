package org.andreaiacono.moviecatalog.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import org.andreaiacono.moviecatalog.core.MoviesCatalog
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.ui.AsyncTaskType
import org.andreaiacono.moviecatalog.util.ImageAdapter
import org.andreaiacono.moviecatalog.ui.PostTaskListener
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.activity.task.FileSystemImageLoaderTask
import org.andreaiacono.moviecatalog.activity.task.NasScanningTask
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.util.thumbNameNormalizer

class MainActivity : PostTaskListener<Any>, AppCompatActivity() {

    val LOG_TAG = this.javaClass.name

    private lateinit var imageGrid: GridView
    private lateinit var moviesCatalog: MoviesCatalog

    override fun onPostTask(result: Any, asyncTaskType: AsyncTaskType, exception: Exception?) {

        if (exception != null) {
            Log.d(LOG_TAG, exception.message, exception)
            val toast = Toast.makeText(
                applicationContext,
                "An error occurred while executing $asyncTaskType: ${exception.message}",
                Toast.LENGTH_LONG
            )
            toast.show()
        } else
            when (asyncTaskType) {
                AsyncTaskType.NAS_IMAGE_LOAD -> {
//                    this.imageGrid.adapter = ImageAdapter(this, result as List<Bitmap>)
                }
                AsyncTaskType.NAS_SCAN -> {
                    moviesCatalog.movies = (result as List<NasMovie>)
                        .map {
                            Movie(
                                it.title,
                                it.date,
                                it.dirName,
                                it.genres,
                                thumbNameNormalizer(it.title)
                            )
                        }
                        .toList()
                    moviesCatalog.saveCatalog()
                }
                AsyncTaskType.FILE_SYSTEM_IMAGE_LOAD -> {
                    val bitmaps = result as List<Bitmap>
                    Log.d(LOG_TAG, "$bitmaps")
                    if (!bitmaps.isEmpty()) {
                        imageGrid.adapter = ImageAdapter(this, bitmaps)
                    } else {
                        // dialog for asking to scan?
                    }
                }
                AsyncTaskType.DUNE_HD_COMMANDER -> {
                    val toast = Toast.makeText(applicationContext, result.toString(), Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        imageGrid = findViewById(R.id.moviesGridView)
        moviesCatalog =
            MoviesCatalog(
                this,
                "smb://192.168.1.90/Volume_1/movies/",
                "http://www.omdbapi.com/",
                "13c1fc2a",
                "192.168.1.83"
            )

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, moviesCatalog.genres)
        val genresListView: ListView = findViewById(R.id.genresListView)
        genresListView.adapter = adapter
        genresListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            moviesCatalog.setGenreFilter(genresListView.getAdapter().getItem(i).toString())
//            imageAdapter.notifyDataSetChanged()
        }
        val nasProgressBar: ProgressBar = findViewById(R.id.nasProgressBar)
        nasProgressBar.visibility = ProgressBar.VISIBLE
        nasProgressBar.max = 100
        nasProgressBar.progress = 0
        FileSystemImageLoaderTask(this, moviesCatalog, nasProgressBar).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_scan -> {
                val nasProgressBar: ProgressBar = findViewById(R.id.nasProgressBar)
                nasProgressBar.visibility = ProgressBar.VISIBLE
                nasProgressBar.max = 100
                nasProgressBar.progress = 0
                NasScanningTask(this, moviesCatalog, nasProgressBar).execute()
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
            R.id.action_delete -> {
                moviesCatalog.deleteAll()
                val toast = Toast.makeText(applicationContext, "All files deleted", Toast.LENGTH_SHORT)
                toast.show()
                true
            }
            R.id.action_ls -> {
                Log.d(LOG_TAG, "Private directory content: \n${filesDir.list().map { "[$it]" }.joinToString("\n")}")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
