package org.andreaiacono.moviecatalog.service

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.andreaiacono.moviecatalog.model.ApiMovie
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.model.Search
import org.andreaiacono.moviecatalog.network.OpenMovieReader
import java.net.URL
import java.util.logging.Logger


class DuneHdService(duneIp: String, nasUrl: String) {

    val LOG_TAG = this.javaClass.name
    val url = "http://$duneIp/cgi-bin/do?cmd=start_playlist_playback&media_url=$nasUrl"

    fun startMovie(movieDir: String, movieFilename: String): String {
        val fullFilename = "$url/$movieDir/$movieFilename"
        Log.d(LOG_TAG, "Starting $fullFilename")
        return URL(fullFilename).readText()
    }

}

