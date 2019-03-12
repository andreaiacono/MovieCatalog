package org.andreaiacono.moviecatalog.core

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.Search
import org.andreaiacono.moviecatalog.service.DuneHdService
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.service.OpenMovieService
import org.andreaiacono.moviecatalog.util.MOVIE_CATALOG_FILENAME
import java.io.*
import java.util.*

val ALL_GENRES = "No Filter"

class MoviesCatalog(val context: Context, nasUrl: String, openMovieUrl: String, openMovieApiKey: String, duneIp: String) {

    val LOG_TAG = this.javaClass.name
    private var genreFilter: String = ALL_GENRES
    var genericFilter: String = ALL_GENRES

    var genres: MutableList<String> = mutableListOf()
    var movies: List<Movie> = listOf()

    private var comparator: Comparator<Movie> = MovieComparator.BY_DATE_DESC
    val nasService = NasService(nasUrl)
    val openMovieService = OpenMovieService(openMovieUrl, openMovieApiKey)
    val duneHdService = DuneHdService(duneIp, nasUrl)

    var sortingGenre: String = ALL_GENRES

    init {
        loadCatalog()
        genres = mutableListOf(ALL_GENRES)
        genres.addAll(movies.flatMap { it.genres }.toList().distinct().sorted())
        Log.d(LOG_TAG, "Loaded movies: $movies")
        Log.d(LOG_TAG, "Loaded genres: $genres")
    }

    fun getCount() = movies.size

    fun saveCatalog() {
        val catalogFileName = "${context.filesDir}/$MOVIE_CATALOG_FILENAME"
        try {
            Log.d(LOG_TAG, "Saving $movies to $catalogFileName")
            ObjectOutputStream(FileOutputStream(catalogFileName)).use { it.writeObject(movies) }
        }
        catch (ex: Exception) {
            Log.e(LOG_TAG, "No catalog file $catalogFileName on device.")
        }
    }

    fun loadCatalog() {
        val catalogFileName = "${context.filesDir}/$MOVIE_CATALOG_FILENAME"
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
            Log.e(LOG_TAG, "No catalog file $catalogFileName on device.", ex)
        }
        movies = movies.sortedWith(MovieComparator.BY_DATE_DESC).take(5)
        Log.d(LOG_TAG, "Movies: ${movies}")
    }

    fun saveBitmap(thumbFilename: String, image: Bitmap) {
        try {
            context.openFileOutput(thumbFilename, Context.MODE_PRIVATE).use {
                image.compress(Bitmap.CompressFormat.PNG, 92, it)
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "An error has occurred saving bitmap on [$thumbFilename]: ${ex.message}")
        }
    }

    fun deleteAll() {
        context.getFilesDir().listFiles().forEach { it.delete() }
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