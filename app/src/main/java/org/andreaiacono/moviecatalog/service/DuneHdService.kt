package org.andreaiacono.moviecatalog.service

import android.util.Log
import java.io.Serializable
import java.net.URL

class DuneHdService(duneIp: String, nasUrl: String) : Serializable {

    val LOG_TAG = this.javaClass.name
    val url = "http://$duneIp/cgi-bin/do?cmd=start_playlist_playback&media_url=$nasUrl"

    fun startMovie(movieDir: String, movieFilename: String): String {
        val fullFilename = "$url/$movieDir/$movieFilename"
        Log.d(LOG_TAG, "Starting $fullFilename")
        return URL(fullFilename).readText()
    }
}

