package org.andreaiacono.moviecatalog.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.TypedValue
import android.widget.*
import android.view.*
import org.andreaiacono.moviecatalog.service.ALL_GENRES
import org.andreaiacono.moviecatalog.model.Movie
import java.util.Comparator


data class MovieBitmap(val movie: Movie, val bitmap: Bitmap, var selected: Boolean = false)


class ImageAdapter(val context: Context, val movieBitmaps: MutableList<MovieBitmap>) : BaseAdapter() {

    val LOG_TAG = this.javaClass.name

    val pxWidth = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics)).toInt()
    val pxHeight = (pxWidth * 1.5).toInt()
    var comparator: MovieComparator = MovieComparator.BY_DATE_ASC
    var filteredBitmaps: MutableList<MovieBitmap> = movieBitmaps.toMutableList()
    var currentSeenFilter = false
    var currentGenreFilter: String = ALL_GENRES

    override fun getCount(): Int = filteredBitmaps.size

    override fun getItem(position: Int): Any? = filteredBitmaps[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(this.context)
            imageView.layoutParams = ViewGroup.LayoutParams(pxWidth, pxHeight)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        }
        else {
            imageView = convertView as ImageView
        }

        imageView.alpha = if (filteredBitmaps[position].selected) 0.5f else 1f
        imageView.setImageBitmap(this.filteredBitmaps[position].bitmap)
        return imageView
    }

    fun filter() {
        filteredBitmaps = movieBitmaps.toMutableList()
        if (currentSeenFilter) {
            filteredBitmaps = filteredBitmaps.filter { !it.movie.seen }.toMutableList()
        }
        if (currentGenreFilter != ALL_GENRES) {
            filteredBitmaps = filteredBitmaps.filter { it.movie.genres.contains(currentGenreFilter) }.toMutableList()
        }
        sort()
        notifyDataSetChanged()
    }

    fun filterBySeen() {
        currentSeenFilter = !currentSeenFilter
        filter()
    }

    fun filterByGenre(genreFilter: String) {
        currentGenreFilter = genreFilter
        filter()
    }

    fun setTitleComparator() {
        comparator = if (comparator === MovieComparator.BY_TITLE_ASC) {
            MovieComparator.BY_TITLE_DESC
        } else {
            MovieComparator.BY_TITLE_ASC
        }
        sort()
        notifyDataSetChanged()
    }

    fun setDateComparator() {
        comparator = if (comparator === MovieComparator.BY_DATE_ASC) {
            MovieComparator.BY_DATE_DESC
        } else {
            MovieComparator.BY_DATE_ASC
        }
        sort()
        notifyDataSetChanged()
    }

    fun search(s: String) {
        filteredBitmaps = movieBitmaps.filter {
            it.movie.title.toLowerCase().contains(s.toLowerCase()) ||
                    it.movie.cast.toString().toLowerCase().contains(s.toLowerCase()) ||
                    it.movie.directors.toString().toLowerCase().contains(s.toLowerCase())
        }.toMutableList()
        sort()
        notifyDataSetChanged()
    }

    private fun sort() = java.util.Collections.sort(filteredBitmaps, comparator)

    fun deleteAll() {
        movieBitmaps.clear()
        filteredBitmaps = mutableListOf()
    }

    fun updateSeenState(updatedMovies: List<Movie>) {
        updatedMovies.forEach { updatedMovie ->
            movieBitmaps.find { adapterMovie -> adapterMovie.movie.title == updatedMovie.title }?.movie?.seen = !updatedMovie.seen
            filteredBitmaps.find { adapterMovie -> adapterMovie.movie.title == updatedMovie.title }?.movie?.seen = !updatedMovie.seen
        }
        notifyDataSetChanged()
    }
}


enum class MovieComparator : Comparator<MovieBitmap> {
    BY_TITLE_ASC {
        override fun compare(m1: MovieBitmap, m2: MovieBitmap): Int {
            return m1.movie.sortingTitle.compareTo(m2.movie.sortingTitle)
        }
    },
    BY_DATE_ASC {
        override fun compare(m1: MovieBitmap, m2: MovieBitmap): Int {
            return m1.movie.date.compareTo(m2.movie.date)
        }
    },
    BY_TITLE_DESC {
        override fun compare(m1: MovieBitmap, m2: MovieBitmap): Int {
            return -BY_TITLE_ASC.compare(m1, m2)
        }
    },
    BY_DATE_DESC {
        override fun compare(m1: MovieBitmap, m2: MovieBitmap): Int {
            return -BY_DATE_ASC.compare(m1, m2)
        }
    }
}