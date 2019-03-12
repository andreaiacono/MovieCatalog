package org.andreaiacono.moviecatalog.util


val MOVIE_EXTENSIONS = setOf("mpg", "mpeg", "mp4", "m4p", "m4v", "avi", "mkv", "webm", "vob", "mov", "qt", "wmv")

fun thumbNameNormalizer(filename: String): String {
    return Regex("\\W").replace(filename, "_") + ".png"
}
