package org.andreaiacono.moviecatalog.service

import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.network.NasReader
import java.io.Serializable


class NasService(url: String) : Serializable {

    private val nasReader = NasReader(url)

    fun getMoviesDirectories(): List<String> = nasReader.getMoviesDirectories()

    fun getTitles(existingMovies: List<Movie>): Pair<List<NasMovie>, List<String>> = nasReader.getMovies(existingMovies)

    fun getThumbnail(movieDir: String) = nasReader.getThumb(movieDir)

    fun getFullImage(movieDir: String) = nasReader.getFullImage(movieDir)
}

