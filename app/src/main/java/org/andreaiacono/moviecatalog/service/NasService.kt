package org.andreaiacono.moviecatalog.service

import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.network.NasReader


class NasService(url: String) {

    private val nasReader = NasReader(url)

    fun getTitles(existingMovies: List<Movie>): Pair<List<NasMovie>,List<String>> = nasReader.getMovies(existingMovies)

    fun getThumbnail(movieDir: String) = nasReader.getThumb(movieDir)

    fun getFullImage(movieDir: String) = nasReader.getFullImage(movieDir)
}

