package org.andreaiacono.moviecatalog.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.andreaiacono.moviecatalog.model.ApiMovie
import org.andreaiacono.moviecatalog.model.Search
import org.andreaiacono.moviecatalog.network.OpenMovieReader


class OpenMovieService(url: String, apiKey: String) {

    private val service = OpenMovieReader(url, apiKey)
    private val jsonMapper = jacksonObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    fun searchMovie(title: String): Search {
        val jsonResult = service.searchMovieInfo(title)
        return jsonMapper.readValue(jsonResult, Search::class.java)
    }

    fun getMovieInfo(imdbId : String): ApiMovie {
        val jsonResult = service.getMovieInfo(imdbId)
        return jsonMapper.readValue(jsonResult, ApiMovie::class.java)
    }
}

