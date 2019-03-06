package org.andreaiacono.moviecatalog.network

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.model.Details
import org.andreaiacono.moviecatalog.model.Movie
import java.util.*
import java.util.logging.Logger


class NasReader(val url: String) {

    private val kotlinXmlMapper = XmlMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private fun getMovies(alreadyPresentNasMovies: List<Movie>): List<Movie> {

        val moviesXml: MutableList<Movie> = mutableListOf()
        val moviesDirs = alreadyPresentNasMovies.map { it.dirName }.toList()

        val smb = SmbFile(url)
        for (file in smb.listFiles()) {

            Logger.getAnonymousLogger().fine("Reading file ${file.name}")
            if (file.isDirectory && !moviesDirs.contains(file.name)) {
                val xmlFiles = file.listFiles().filter { it.name.endsWith(".xml") }.toList()
                if (xmlFiles.isEmpty()) {
                    moviesXml.add(Movie(file.name, Date(file.date), file.name))
                }
                else {
                    val xmlContent = xmlFiles[0].inputStream.readBytes().toString(Charsets.UTF_8)
                    val nasMovie = kotlinXmlMapper.readValue(xmlContent, Details::class.java).movie
                    moviesXml.add(
                        Movie(
                            nasMovie.title,
                            if (nasMovie.date.time != 0L) nasMovie.date else Date(file.date),
                            file.name
                        )
                    )
                }
            }
        }
        return moviesXml
    }

    fun getNewMovies(nasMovies: List<Movie>): List<Movie> {
        return getMovies(nasMovies)
    }

    fun getAllMovies(): List<Movie> {
        return getMovies(listOf())
    }

}
