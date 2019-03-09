package org.andreaiacono.moviecatalog.util

fun thumbNameNormalizer(filename: String): String {
    return Regex("\\W").replace(filename, "_") + ".png"
}
