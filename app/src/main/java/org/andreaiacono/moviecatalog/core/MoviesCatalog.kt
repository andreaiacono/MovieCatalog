package org.andreaiacono.moviecatalog.core

import android.content.Context
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.NasService
import java.util.Comparator

class MoviesCatalog(val ctx: Context, nasUrl: String) {

    private var genreFilter: String? = null
    private var genericFilter: String? = null
    private var genres: MutableList<String>? = null
    private var genresArray: Array<String>? = null
    private var movies: MutableList<Movie>? = null
    private var displayedMovies: MutableList<Movie>? = null
    private var comparator: Comparator<Movie> = MovieComparator.BY_DATE_DESC

    var nasService: NasService = NasService(nasUrl)


    fun scanMovies() =  nasService.getAllTitles()
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