package org.andreaiacono.moviecatalog.network

import java.net.URL

class OpenMovieReader(url: String, apiKey: String) {

    val searchUrl = url + "?apikey=$apiKey&type=movie&s="
    val movieUrl = url + "?apikey=$apiKey&type=movie&i="

    fun searchMovieInfo(title: String): String = URL(searchUrl + title.replace(" ", "%20")).readText()

    fun getMovieInfo(movieId: String): String = URL(movieUrl + movieId).readText()
}