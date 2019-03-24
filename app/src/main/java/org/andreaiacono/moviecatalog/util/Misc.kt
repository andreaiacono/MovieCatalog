package org.andreaiacono.moviecatalog.util

import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager
import org.andreaiacono.moviecatalog.model.NasMovie


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
    return  (width / scaleFactor).toInt()
}