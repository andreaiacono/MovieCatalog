package org.andreaiacono.moviecatalog.service

import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.network.NasReader


class NasService(url: String) {

    private val nasReader = NasReader(url)

    fun getAllTitles(): List<Movie> = nasReader.getAllMovies()

    fun getNewTitles(existingMovies: List<Movie>): List<Movie> = nasReader.getNewMovies(existingMovies)
}

