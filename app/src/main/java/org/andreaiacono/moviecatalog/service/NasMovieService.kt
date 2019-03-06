package org.andreaiacono.moviecatalog.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.andreaiacono.moviecatalog.model.Details
import org.andreaiacono.moviecatalog.model.NasMovie


class NasMovieService {

    private val kotlinXmlMapper = XmlMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getMovie(xmlContent : String, dirName: String): NasMovie {
        val nasMovieInfo: NasMovie = kotlinXmlMapper.readValue(xmlContent, Details::class.java).movie
        return NasMovie(nasMovieInfo.title, nasMovieInfo.date, dirName)
    }
}

