package org.andreaiacono.moviecatalog.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Search(
    @JsonProperty("Search")
    val search: List<ApiMovie> = emptyList()
)

data class ApiMovie(

    @JsonProperty("Title")
    val title: String,
    val year: Int = 1970,

    @JsonProperty("Rated")
    val rated: String = "",

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd MMM yyyy"
    )
    @JsonProperty("Released")
    val released: Date = Date(0L),

    @JsonProperty("Runtime")
    val runtime: String = "",

    @JsonProperty("Genre")
    val genre: String = "",

    @JsonProperty("Director")
    val director: String = "",

    @JsonProperty("Writer")
    val writer: String = "",

    @JsonProperty("Actor")
    val actors: String = "",

    @JsonProperty("Plot")
    val plot: String = "",

    @JsonProperty("Language")
    val language: String = "",

    @JsonProperty("Country")
    val country: String = "",

    @JsonProperty("Awards")
    val awards: String = "",

    @JsonProperty("Poster")
    val poster: String = "",

    @JsonProperty("Metascore")
    val metascore: Int = 0,

    val imdbRating: Float = 0.0f,

    val imdbVotes: String = "",

    @JsonProperty("imdbID")
    val imdbId: String = "",

    @JsonProperty("Type")
    val type: String = "",

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd MMM yyyy"
    )
    @JsonProperty("DVD")
    val dvd: Date = Date(0),

    @JsonProperty("BoxOffice")
    val boxOffice: String = "",

    @JsonProperty("Production")
    val production: String = "",

    @JsonProperty("Website")
    val website: String = "",

    @JsonProperty("Response")
    val response: String = ""
)