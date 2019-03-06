package org.andreaiacono.moviecatalog.network

import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.service.NasMovieService
import java.util.*
import java.util.logging.Logger


class NasInfoReader(val url: String, val username: String, val password: String) {

    val auth = NtlmPasswordAuthentication("$username:$password")

    private fun getTitles(alreadyPresentNasMovies: List<Movie>): List<Movie> {

        val nasMovies: MutableList<NasMovie> = mutableListOf()
        val moviesDirs = alreadyPresentNasMovies.map{it.dirName}.toList()

        val smb = SmbFile(url)
        for (file in smb.listFiles()) {

            Logger.getAnonymousLogger().fine("Reading file ${file.name}")
            if (file.isDirectory && !moviesDirs.contains(file.name)) {

                val xmlFiles = file.listFiles().filter { it.name.endsWith(".xml") }.toList()
                if (xmlFiles.isEmpty()) {
                    nasMovies.add(NasMovie(file.name, Date(file.date), file.name))
                }
                else {
                    val xmlContent = xmlFiles[0].inputStream.readBytes().toString()
                    nasMovies.add(NasMovieService().getMovie(xmlContent, file.name))
                }
            }
        }

        return nasMovies.map{ nasMovie -> Movie(nasMovie.title, nasMovie.date, nasMovie.dirName)}
    }

    fun getNewTitles(nasMovies: List<Movie>): List<Movie> {
        return getTitles(nasMovies)
    }

    fun getAllTitles(): List<Movie> {
        return getTitles(listOf())
    }

}
