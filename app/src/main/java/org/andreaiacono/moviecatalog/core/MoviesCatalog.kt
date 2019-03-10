package org.andreaiacono.moviecatalog.core

import android.app.Activity

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.andreaiacono.moviecatalog.activity.MainActivity
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.Search
import org.andreaiacono.moviecatalog.service.DuneHdService
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.service.OpenMovieService
import org.andreaiacono.moviecatalog.util.MOVIE_CATALOG_FILENAME
import java.io.*
import java.util.*

object MoviesCatalog {

    val LOG_TAG = this.javaClass.name

    val ALL_GENRES = "No Filter"
    val THUMBS_DIR = "thumbs"
    private var genreFilter: String = ALL_GENRES
    var genericFilter: String = ALL_GENRES

    var genres: List<String> = listOf()
    var movies: List<Movie> = listOf()

    private var comparator: Comparator<Movie> = MovieComparator.BY_DATE_DESC
    lateinit var nasService: NasService
    lateinit var openMovieService: OpenMovieService
    lateinit var duneHdService: DuneHdService
    lateinit var main: MainActivity

    var sortingGenre: String = ALL_GENRES

    fun init( main: MainActivity, nasUrl: String, openMovieUrl: String, openMovieApiKey: String, duneIp: String) {
        this.main = main
        loadCatalog()
        nasService = NasService(nasUrl)
        openMovieService = OpenMovieService(openMovieUrl, openMovieApiKey)
        duneHdService = DuneHdService(duneIp, nasUrl)

        genres = movies.flatMap { it.genres }.toList().distinct().sorted()
        Log.d(LOG_TAG, "Loaded movies: $movies")
        Log.d(LOG_TAG, "Loaded genres: $genres")
    }

    fun getCount() = movies.size

    fun setGenreFilter(genre: String) {
        sortingGenre = genre
    }

    fun searchMovie(title: String): Search {
        return openMovieService.searchMovie(title)
    }

    fun saveCatalog() {
        val catalogFileName = "${main.application.applicationContext.filesDir}/$MOVIE_CATALOG_FILENAME"
        try {
            Log.d(LOG_TAG, "Saving $movies to $catalogFileName")
            ObjectOutputStream(FileOutputStream(catalogFileName)).use { it.writeObject(movies) }
        }
        catch (ex: Exception) {
            Log.e(LOG_TAG, "No catalog file $catalogFileName on device.")
        }
    }

    fun loadCatalog() {
        val catalogFileName = "${main.application.applicationContext.filesDir}/$MOVIE_CATALOG_FILENAME"
        try {
            ObjectInputStream(FileInputStream(catalogFileName)).use {
                val catalog = it.readObject()
                when (catalog) {
                    is List<*> -> movies = catalog as List<Movie>
                    else -> {
                        Log.e(LOG_TAG, "Deserialization failed.")
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "No catalog file $catalogFileName on device.")
        }
    }

    fun saveBitmap(thumbFilename: String, image: Bitmap) {
        try {
            main.application.applicationContext.openFileOutput(thumbFilename, Context.MODE_PRIVATE).use {
                image.compress(Bitmap.CompressFormat.PNG, 92, it)
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "An error has occurred saving bitmap on [$thumbFilename]: ${ex.message}")
        }
    }

    fun deleteAll() {
        val ctx = main.application.applicationContext
        ctx.getFilesDir().listFiles().forEach { it.delete() }
    }
}

private enum class MovieComparator : Comparator<Movie> {
    BY_TITLE_ASC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return m1.title.compareTo(m2.title)
        }
    },
    BY_DATE_ASC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return m1.date.compareTo(m2.date)
        }
    },
    BY_TITLE_DESC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return -BY_TITLE_ASC.compare(m1, m2)
        }
    },
    BY_DATE_DESC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return -BY_DATE_ASC.compare(m1, m2)
        }
    }
}