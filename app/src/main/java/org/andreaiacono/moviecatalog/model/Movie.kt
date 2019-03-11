package org.andreaiacono.moviecatalog.model

import java.io.Serializable
import java.util.*

data class Movie (
    val title: String,
    val date: Date,
    val dirName: String,
    val genres: List<String> = listOf(),
    val thumbName: String
) : Serializable

val EMPTY_MOVIE = Movie("", Date(0L), "", thumbName = "")