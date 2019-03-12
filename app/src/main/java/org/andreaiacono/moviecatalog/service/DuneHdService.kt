package org.andreaiacono.moviecatalog.service

import android.util.Log
import java.io.Serializable
import java.net.URL

class DuneHdService(duneIp: String, val nasUrl: String) : Serializable {

    val LOG_TAG = this.javaClass.name
    val url = "http://$duneIp/cgi-bin/do?cmd=launch_media_url&position=0&media_url="



    fun startMovie(movieDir: String, movieFilename: String): String {
        val fullFilename = "$url$nasUrl$movieDir$movieFilename"
        Log.d(LOG_TAG, "Starting $fullFilename")
        return URL(fullFilename).readText()
    }
}

