package org.andreaiacono.moviecatalog.model

import java.io.Serializable
import java.util.*

data class Movie(
    val title: String,
    val sortingTitle: String,
    val date: Date,
    val deviceThumbName: String,
    val nasDirName: String,
    val nasVideoFileName: String,
    val genres: List<String> = listOf(),
    val cast: List<String> = listOf(),
    val directors: List<String> = listOf(),
    var seen: Boolean
) : Serializable

val EMPTY_MOVIE = Movie("", "", Date(0L), "", "", "", seen = true)