package org.andreaiacono.moviecatalog.core

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.DuneHdService
import org.andreaiacono.moviecatalog.service.NasService
import org.andreaiacono.moviecatalog.util.MOVIE_CATALOG_FILENAME
import java.io.*


val ALL_GENRES = "No Filter"

class MoviesCatalog(
    val context: Context,
    nasUrl: String,
    duneIp: String
) {
    val LOG_TAG = this.javaClass.name

    var genres: MutableList<String> = mutableListOf()
    var movies: List<Movie> = listOf()
    var hasNoData = false

    val nasService = NasService(nasUrl)
    val duneHdService = DuneHdService(duneIp, nasUrl)

    init {
        loadCatalog()
        updateGenres()
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

    private fun loadCatalog() {
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
            Log.d(LOG_TAG, "Movies: ${movies}")
        }
        catch (ex: Exception) {
            Log.e(LOG_TAG, "No catalog file $MOVIE_CATALOG_FILENAME on private dir.", ex)
            hasNoData = true
        }
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

    fun updateGenres() {
        genres.clear()
        genres.add(ALL_GENRES)
        genres.addAll(movies.flatMap { it.genres }.toList().distinct().sorted())
    }


}
