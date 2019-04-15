package org.andreaiacono.moviecatalog.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.task.DeviceImageLoaderTask
import org.andreaiacono.moviecatalog.task.NasScanningTask
import org.andreaiacono.moviecatalog.service.MoviesCatalog
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.task.AsyncTaskType
import org.andreaiacono.moviecatalog.task.PostTaskListener
import java.io.Serializable
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.andreaiacono.moviecatalog.model.Config
import android.widget.Toast
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.util.*


class MainActivity : PostTaskListener<Any>, AppCompatActivity() {

    val LOG_TAG = this.javaClass.name

    private lateinit var config: Config
    private lateinit var gridView: GridView
    private lateinit var genresListView: ListView
    private lateinit var moviesCatalog: MoviesCatalog
    private lateinit var movieBitmaps: ArrayList<MovieBitmap>
    private lateinit var genresAdapter: ArrayAdapter<String>
    private lateinit var imageAdapter: ImageAdapter

    override fun onPostTask(result: Any, asyncTaskType: AsyncTaskType, exception: Exception?) {

        if (exception != null) {
            Log.d(LOG_TAG, exception.message, exception)
            longToast("An error occurred while executing $asyncTaskType: ${exception.message}")
        }
        else
            when (asyncTaskType) {
                AsyncTaskType.NAS_SCAN -> {

                    val newMoviesCount = moviesCatalog.saveNewMoviesOnDevice(result as List<NasMovie>)
                    if (newMoviesCount > 0) {
                        longToast("New movies found: $newMoviesCount")
                        val progressBar: ProgressBar = findViewById(R.id.indefiniteProgressBar)
                        DeviceImageLoaderTask(this, moviesCatalog, progressBar).execute()
                    }
                    else {
                        shortToast("No new movies found")
                    }
                }
                AsyncTaskType.DEVICE_IMAGE_LOAD -> {
                    movieBitmaps = result as ArrayList<MovieBitmap>
                    if (!movieBitmaps.isEmpty()) {
                        imageAdapter = ImageAdapter(this, movieBitmaps)
                        imageAdapter.setDateComparator()
                        gridView.adapter = imageAdapter
                    }
                }
                AsyncTaskType.DUNE_HD_COMMANDER -> {
                    shortToast(result.toString())
                }
                AsyncTaskType.NAS_MOVIE_UPDATE -> {
                    val adapter = gridView.adapter as ImageAdapter
                    val updatedMovies = result as List<Movie>
                    adapter.updateSeenState(updatedMovies)
                    shortToast("${updatedMovies.size} movies marked as seen")
                }
                else -> {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        config = loadConfig()
        moviesCatalog = MoviesCatalog(
            this,
            config.nasUrl,
            config.duneIp
        )

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        genresListView = findViewById(R.id.genresListView)
        genresAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, moviesCatalog.genres)
        genresListView.adapter = genresAdapter
        genresListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            imageAdapter.filterByGenre(genresListView.adapter.getItem(i).toString())
        }

        gridView = findViewById(R.id.moviesGridView)
        gridView.numColumns = computeColumns(resources, windowManager)

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, v, position, _ ->
            val fullScreenIntent = Intent(v.context, MovieDetailActivity::class.java)
            fullScreenIntent.putExtra("movie", (imageAdapter.getItem(position) as MovieBitmap).movie)
            fullScreenIntent.putExtra("NasService", moviesCatalog.nasService as Serializable)
            fullScreenIntent.putExtra("DuneHdService", moviesCatalog.duneHdService as Serializable)
            startActivity(fullScreenIntent)
        }
        gridView.choiceMode = GridView.CHOICE_MODE_MULTIPLE_MODAL
        gridView.setMultiChoiceModeListener(GridViewMultiSelector(this, gridView, moviesCatalog))

        val fileSystemProgressBar: ProgressBar = findViewById(R.id.horizontalProgressBar)
        DeviceImageLoaderTask(this, moviesCatalog, fileSystemProgressBar).execute()

        if (moviesCatalog.hasNoData) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("No movies found on this device.\n\nScan your NAS!")
            builder.setCancelable(false)
            builder.setPositiveButton("Close", null)
            val dialog = builder.show()
            val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
            messageView.gravity = Gravity.CENTER
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        // changes default icon
        val searchImgId = android.support.v7.appcompat.R.id.search_button
        val v = searchView.findViewById(searchImgId) as ImageView
        v.setImageResource(R.drawable.ic_search)

        // sets search autocompletion
        val searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        val suggestionsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, moviesCatalog.getSearchSuggestions())
        searchAutoComplete.setAdapter(suggestionsAdapter)
        searchAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, itemIndex, _ ->
                val queryString = adapterView.getItemAtPosition(itemIndex) as String
                searchAutoComplete.setText("" + queryString)
            }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                imageAdapter.search(s)
                return false
            }
            override fun onQueryTextChange(s: String): Boolean {
                imageAdapter.search(s)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_scan -> {
                val horizontalProgressBar: ProgressBar = findViewById(R.id.horizontalProgressBar)
                NasScanningTask(this, moviesCatalog, horizontalProgressBar).execute()
                true
            }
            R.id.action_info -> {
                val builder = AlertDialog.Builder(this)
                val message = "Ip Address Dune HD: ${config.duneIp}" +
                        "\nNAS Url: ${config.nasUrl}" +
                        "\nSaved Movies: " + moviesCatalog.getCount()
                builder.setMessage(message).setTitle(R.string.info_title)
                val dialog = builder.create()
                dialog.show()
                true
            }
            R.id.action_delete -> {
                moviesCatalog.deleteAll()
                imageAdapter.deleteAll()
                genresAdapter.notifyDataSetChanged()
                imageAdapter.notifyDataSetChanged()
                shortToast("All files deleted")
                true
            }
            R.id.action_debugInfo -> {
                val builder = AlertDialog.Builder(this)
                val title = builder.setMessage(getDebugInfo(config, this.filesDir)).setTitle(R.string.info_title)
                val dialog = builder.create()
                dialog.show()
                true
            }
            R.id.menuSortByTitle -> {
                imageAdapter.setTitleComparator()
                true
            }
            R.id.menuSortByDate -> {
                imageAdapter.setDateComparator()
                true
            }
            R.id.action_filter_not_seen -> {
                imageAdapter.filterBySeen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadConfig(): Config {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(resources.openRawResource(R.raw.config), Config::class.java)
    }

    private fun shortToast(message: String) {
        toast(message, Toast.LENGTH_SHORT)
    }

    private fun longToast(message: String) {
        toast(message, Toast.LENGTH_LONG)
    }

    private fun toast(message: String, length: Int) {
        val toast = Toast.makeText(applicationContext, message, length)
        toast.show()
    }
}
