package org.andreaiacono.moviecatalog

import org.andreaiacono.moviecatalog.service.OpenMovieService
import org.junit.Test

class OpenMovieServiceTest {
    @Test
    fun calling() {
        val service = OpenMovieService("http://www.omdbapi.com/", "13c1fc2a")

        val searchResults = service.searchMovie("blade runner")
        val movie = service.getMovieInfo(searchResults.search[0].imdbId)

        println(movie.title)
    }
}
