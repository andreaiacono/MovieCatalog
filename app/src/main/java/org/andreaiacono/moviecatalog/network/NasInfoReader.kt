package org.andreaiacono.moviecatalog.network

import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.util.XmlMovieMapper
import java.util.*
import java.util.logging.Logger


data class MovieTitle(val title: String, val date: Date)

class NasInfoReader(val url: String, val username: String, val password: String) {

    val auth = NtlmPasswordAuthentication("$username:$password")

    private fun getTitles(alreadyPresentMovies: List<Movie>): List<Movie> {

        val movies: MutableList<Movie> = mutableListOf()
        val moviesDirs = alreadyPresentMovies.map{it.dirName}.toList()

        val smb = SmbFile(url)
        for (file in smb.listFiles()) {

            Logger.getAnonymousLogger().fine("Reading file ${file.name}")
            if (file.isDirectory && !moviesDirs.contains(file.name)) {

                val xmlFiles = file.listFiles().filter { it.name.endsWith(".xml") }.toList()
                if (xmlFiles.isEmpty()) {
                    movies.add(Movie(file.name, Date(file.date), file.name))
                }
                else {
                    val xmlContent = xmlFiles[0].inputStream.readBytes().toString()
                    movies.add(XmlMovieMapper().getMovie(xmlContent, file.name))
                }
            }
        }
        return movies
    }

    fun getNewTitles(movies: List<Movie>): List<Movie> {
        return getTitles(movies)
    }

    fun getAllTitles(): List<Movie> {
        return getTitles(listOf())
    }

}
