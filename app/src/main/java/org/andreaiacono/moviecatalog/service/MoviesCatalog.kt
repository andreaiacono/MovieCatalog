package org.andreaiacono.moviecatalog.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.andreaiacono.moviecatalog.activity.MainActivity
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.task.NasMoviesUpdaterTask
import org.andreaiacono.moviecatalog.util.MOVIE_CATALOG_FILENAME
import org.andreaiacono.moviecatalog.util.thumbNameNormalizer
import java.io.*


val ALL_GENRES = "No Filter"

class MoviesCatalog(val context: Context, nasUrl: String, duneIp: String) {

    val LOG_TAG = this.javaClass.name

    val nasService = NasService(nasUrl)
    val duneHdService = DuneHdService(duneIp, nasUrl)

    var genres: MutableList<String> = mutableListOf()
    var movies: MutableList<Movie> = mutableListOf()
    var suggestions: MutableSet<String> = mutableSetOf()
    var hasNoData = false

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
            Log.d(LOG_TAG, "Saving catalog to $catalogFileName")
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
                    is List<*> -> movies = catalog as MutableList<Movie>
                    else -> {
                        Log.e(LOG_TAG, "Deserialization failed.")
                    }
                }
            }
            setSuggestions()
            movies.forEach { println("${it.title} ${it.seen}") }
            Log.d(LOG_TAG, "Movies: ${movies}")
        }
        catch (ex: Exception) {
            Log.e(LOG_TAG, "No catalog file $MOVIE_CATALOG_FILENAME on private dir.", ex)
            hasNoData = true
        }
    }

    fun setSuggestions() {
        suggestions.clear()
        movies.forEach {
            suggestions.add(it.title)
            suggestions.addAll(it.cast)
            suggestions.addAll(it.directors)
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
        movies.clear()
        genres.clear()
    }

    fun updateGenres() {
        genres.clear()
        genres.add(ALL_GENRES)
        genres.addAll(movies.flatMap { it.genres }.toList().distinct().sorted())
    }

    fun saveNewMoviesOnDevice(taskResult: List<NasMovie>): Int {

        val newMovies = taskResult
            .map {
                Movie(
                    it.title,
                    it.sortingTitle!!,
                    it.date,
                    thumbNameNormalizer(it.title),
                    it.dirName,
                    it.videoFilename,
                    it.genres,
                    it.cast,
                    it.directors,
                    it.seen
                )
            }
            .toList()
        if (! newMovies.isEmpty()) {
            movies.addAll(newMovies)
            updateGenres()
            saveCatalog()
            setSuggestions()
        }

        return newMovies.size
    }

    fun getSearchSuggestions(): Array<String> = suggestions.toTypedArray()

    fun setAsSeen(moviesToUpdate: List<Movie>, mainActivity: MainActivity) {
        moviesToUpdate.forEach { movies.find { movie -> movie.title == it.title }?.seen = !it.seen }
        saveCatalog()
        NasMoviesUpdaterTask(mainActivity, nasService, moviesToUpdate).execute()
    }
}
