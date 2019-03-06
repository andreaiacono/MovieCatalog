package org.andreaiacono.moviecatalog.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.andreaiacono.moviecatalog.model.Details
import org.andreaiacono.moviecatalog.model.Movie


class XmlMovieMapper {

    private val kotlinXmlMapper = XmlMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getMovie(xmlContent : String, dirName: String): Movie {
        val movieInfo: Movie = kotlinXmlMapper.readValue(xmlContent, Details::class.java).movie
        return Movie(movieInfo.title, movieInfo.date, dirName)
    }

}

