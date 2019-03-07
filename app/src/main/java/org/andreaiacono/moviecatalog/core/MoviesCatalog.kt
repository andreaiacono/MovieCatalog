package org.andreaiacono.moviecatalog.core

import android.content.Context
import android.provider.MediaStore
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.NasService
import java.util.*

class MoviesCatalog(val ctx: Context, nasUrl: String) {

    private var genreFilter: String? = null
    private var genericFilter: String? = null
    private var genres: MutableList<String>? = null
    private var genresArray: Array<String>? = null
    private val movies: MutableList<Movie> = mutableListOf()
    private var displayedMovies: MutableList<Movie>? = null
    private var comparator: Comparator<Movie> = MovieComparator.BY_DATE_DESC

    var nasService: NasService = NasService(nasUrl)

    var sortingGenre: String = "No Filter"

    fun scanMovies() =  nasService.getAllTitles()

    fun getCount() = movies.size
    fun getSampleGenres(): List<String> {
        return listOf(
            "Disaster",
            "Drama",
            "Romantic",
            "Educational",
            "Fantasy",
            "Gangster",
            "History",
            "Horror",
            "Military",
            "Mystery",
            "Nature",
            "Documentary",
            "Politics",
            "Road movie",
            "Romance",
            "Science",
            "Science fiction",
            "Spiritual",
            "Sports",
            "Spy",
            "Teen",
            "Variety",
            "War"
        )
    }
    fun getGenres(): List<String> = getSampleGenres()

    fun setGenreFilter(genre: String) {
        sortingGenre = genre
    }
}

private enum class MovieComparator : Comparator<Movie> {
    BY_TITLE_ASC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return m1.title.compareTo(m2.title)
        }
    },
    BY_DATE_ASC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return m1.date.compareTo(m2.date)
        }
    },
    BY_TITLE_DESC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return -BY_TITLE_ASC.compare(m1, m2)
        }
    },
    BY_DATE_DESC {
        override fun compare(m1: Movie, m2: Movie): Int {
            return -BY_DATE_ASC.compare(m1, m2)
        }
    }
}