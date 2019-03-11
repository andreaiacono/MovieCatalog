package org.andreaiacono.moviecatalog.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.model.fromXml
import java.io.InputStream
import java.io.Serializable
import java.util.*

class NasReader(val url: String) : Serializable {

    val LOG_TAG = this.javaClass.name

    fun getMovies(alreadyPresentNasMovies: List<Movie>): Pair<List<NasMovie>, List<String>> {

        val existingMoviesDirs = alreadyPresentNasMovies.map { it.dirName }.toList()
        val nasMovies: MutableList<NasMovie> = mutableListOf()
        val notFoundMovies: MutableList<String> = mutableListOf()

        val moviesRoot = SmbFile(url)
        for (movieDir in moviesRoot.listFiles().take(1000)) {

            Log.d(LOG_TAG, "Reading file ${movieDir.name}")
            if (movieDir.isDirectory && !existingMoviesDirs.contains(movieDir.name)) {
                val xmlFiles = movieDir
                    .listFiles()
                    .filter { !it.name.startsWith(".") }
                    .filter { it.name.toLowerCase().endsWith(".xml") }
                    .toList()

                try {
                    if (!xmlFiles.isEmpty()) {
                        // assumes there's only one xml file in each dir
                        Log.d(LOG_TAG, "xmlFiles: $xmlFiles")
                        val xmlContent = xmlFiles[0].inputStream.readBytes().toString(Charsets.UTF_8)
                        val xmlMovie = fromXml(xmlContent)
                        nasMovies.add(
                            NasMovie(
                                xmlMovie.title,
                                xmlMovie.sortingTitle ?: xmlMovie.title,
                                if (xmlMovie.date.time > 0L) xmlMovie.date else Date(movieDir.date),
                                xmlMovie.genres,
                                movieDir.name
                            )
                        )
                    }
                    else {
                        notFoundMovies.add(movieDir.name)
                        Log.i(LOG_TAG, "Xml file not found in [$movieDir]")
                    }
                } catch (ex: Throwable) {
                    Log.e(LOG_TAG, "Error occurred on $movieDir: ${ex.message}")
                }
            }
        }
        return Pair(nasMovies, notFoundMovies)
    }

    private fun getFullImage(dirName: String, filename: String): Bitmap {
        val fullName = "$url$dirName$filename"
        Log.d(LOG_TAG, "Loading image $fullName")
        return urlImageToBitmap(SmbFile(fullName).inputStream)
    }

    fun getThumb(dirName: String): Bitmap = getFullImage(dirName, "folder.jpg")

    fun getFullImage(dirName: String): Bitmap = getFullImage(dirName, "about.jpg")

    private fun urlImageToBitmap(imageStream: InputStream): Bitmap = BitmapFactory.decodeStream(imageStream)

}
