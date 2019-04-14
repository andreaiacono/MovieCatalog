package org.andreaiacono.moviecatalog.task

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.service.MoviesCatalog
import org.andreaiacono.moviecatalog.model.NasMovie
import org.andreaiacono.moviecatalog.model.fromXml
import org.andreaiacono.moviecatalog.util.MOVIE_EXTENSIONS
import org.andreaiacono.moviecatalog.util.thumbNameNormalizer
import java.util.*

internal class NasScanningTask(taskListener: PostTaskListener<Any>, val moviesCatalog: MoviesCatalog, val horizontalProgressBar: ProgressBar) :
    AsyncTask<String, Int, Void>() {

    val asyncTaskType = AsyncTaskType.NAS_SCAN

    private var LOG_TAG = this.javaClass.name
    private var exception: Exception? = null
    private var movies: MutableList<NasMovie> = mutableListOf()
    private var postTaskListener: PostTaskListener<Any> = taskListener
    var isReady: Boolean = false

    lateinit var movieDirs: Array<SmbFile>

    override fun onPreExecute() {
        super.onPreExecute()
        object : Thread() {
            override fun run() {
                try {
                    movieDirs = moviesCatalog.nasService.getMoviesDirectories()
                    horizontalProgressBar.max = movieDirs.size
                } catch (ex: Exception) {
                    exception = ex
                }
                isReady = true
            }
        }.start()

        while (!isReady) {
            Thread.sleep(100)
        }
        horizontalProgressBar.bringToFront()
        horizontalProgressBar.progress = 0
        horizontalProgressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg url: String): Void? {
        if (exception != null) {
            // if there was an error in onPreExecute(), just stops here
            return null
        }
        val existingDirNames = moviesCatalog.movies.map { it.nasDirName }.toList()

        try {
            movieDirs.take(50).forEachIndexed { index, movieDir ->

                publishProgress(index)
                Log.d(LOG_TAG, "Reading file ${movieDir.name}")
                if (movieDir.isDirectory && !existingDirNames.contains(movieDir.name)) {
                    val xmlFiles = movieDir
                        .listFiles()
                        .filter { !it.name.startsWith(".") }
                        .filter { it.name.toLowerCase().endsWith(".xml") }
                        .toList()

                    val movieFiles = movieDir
                        .listFiles()
                        .filter { it.name.takeLast(3).toLowerCase() in MOVIE_EXTENSIONS }
                        .toList()
                    if (movieFiles.size != 1) {
                        Log.e(LOG_TAG, "Video files found: $movieFiles")
                    }

                    try {
                        if (!xmlFiles.isEmpty()) {

                            // assumes there's only one xml file in each dir
                            val xmlContent = xmlFiles[0].inputStream.readBytes().toString(Charsets.UTF_8)
                            val xmlMovie = fromXml(xmlContent)
                            Log.d(LOG_TAG, xmlMovie.toString())
                            movies.add(
                                NasMovie(
                                    xmlMovie.title,
                                    xmlMovie.sortingTitle ?: xmlMovie.title,
                                    if (xmlMovie.date.time > 0L) xmlMovie.date else Date(movieDir.listFiles("folder.jpg").first().date),
                                    xmlMovie.genres,
                                    xmlMovie.cast,
                                    xmlMovie.directors,
                                    xmlMovie.year,
                                    movieDir.name,
                                    movieFiles[0].name
                                )
                            )
                            val thumbFilename = thumbNameNormalizer(xmlMovie.title)
                            Log.d(LOG_TAG, "Saving $thumbFilename (nasDirName=${movieDir.name})")
                            moviesCatalog.saveBitmap(
                                thumbFilename,
                                moviesCatalog.nasService.getThumbnail(movieDir.name)
                            )
                        }
                        else {
                            Log.e(LOG_TAG, "Xml file not found in [$movieDir]")
                        }
                    } catch (ex: Throwable) {
                        Log.e(LOG_TAG, "Error occurred on $movieDir: ${ex.message}")
                    }
                }
            }
        } catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(values[0])
        horizontalProgressBar.progress = values[0]!!.toInt()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postTaskListener.onPostTask(movies, asyncTaskType, exception)
        horizontalProgressBar.visibility = View.GONE
    }
}