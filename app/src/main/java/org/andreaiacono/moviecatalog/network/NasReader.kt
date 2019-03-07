package org.andreaiacono.moviecatalog.network

import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.fromXml
import java.util.*
import java.util.logging.Logger


class NasReader(val url: String) {

    private fun getMovies(alreadyPresentNasMovies: List<Movie>): List<Movie> {

        val moviesXml: MutableList<Movie> = mutableListOf()
        val existingMoviesDirs = alreadyPresentNasMovies.map { it.dirName }.toList()

        val moviesRoot = SmbFile(url)
        for (movieDir in moviesRoot.listFiles().take(1)) {

            Logger.getAnonymousLogger().fine("Reading file ${movieDir.name}")
            if (movieDir.isDirectory && !existingMoviesDirs.contains(movieDir.name)) {
                val xmlFiles = movieDir.listFiles().filter { it.name.endsWith(".xml") }.toList()
                if (xmlFiles.isEmpty()) {
                    moviesXml.add(Movie(movieDir.name, Date(movieDir.date), movieDir.name))
                }
                else {
                    // assumes there's only one xml file in each dir
                    val xmlContent = xmlFiles[0].inputStream.readBytes().toString(Charsets.UTF_8)
                    val nasMovie = fromXml(xmlContent)
                    moviesXml.add(
                        Movie(
                            nasMovie.title,
                            if (nasMovie.date.time > 0L) nasMovie.date else Date(movieDir.date),
                            movieDir.name
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
