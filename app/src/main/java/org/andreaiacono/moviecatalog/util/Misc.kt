package org.andreaiacono.moviecatalog.util

import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager
import org.andreaiacono.moviecatalog.model.Config
import org.andreaiacono.moviecatalog.model.NasMovie
import java.io.File


val MOVIE_EXTENSIONS = setOf("mpg", "mpeg", "mp4", "m4p", "m4v", "avi", "mkv", "webm", "vob", "mov", "qt", "wmv")

fun thumbNameNormalizer(filename: String): String {
    return Regex("\\W").replace(filename, "_") + ".png"
}

fun getInfoFromNasMovie(movie: NasMovie) = movie.title + " " +
        movie.directors.joinToString { it } + " " +
        movie.cast.joinToString { it } + " " +
        movie.year


fun computeColumns(resources: Resources, windowManager: WindowManager): Int {
    val scaleFactor = resources.displayMetrics.density * 180
    val size = Point()
    windowManager.defaultDisplay.getSize(size)
    val width = size.x
    return (width / scaleFactor).toInt()
}

fun getDebugInfo(config: Config, filesDir: File): String {

    val runtime = Runtime.getRuntime()
    val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    val maxHeapSizeInMB = runtime.maxMemory() / (1024 * 1024)
    val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB

    return  "Config:" +
                "\n\tNas URL: ${config.nasUrl}" +
                "\n\tDune IP: ${config.duneIp}\n\n" +
            "Memory info:" +
            "\n\tUsed Memory (MB): $usedMemInMB" +
            "\n\tFree Memory (MB): $availHeapSizeInMB\n\n" +
            "Private directory content:\n" +
            filesDir.list().map { "\t[$it]" }.joinToString("\n")
}