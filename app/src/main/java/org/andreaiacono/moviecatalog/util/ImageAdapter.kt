package org.andreaiacono.moviecatalog.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.util.TypedValue
import android.widget.*
import android.view.*
import org.andreaiacono.moviecatalog.core.ALL_GENRES
import org.andreaiacono.moviecatalog.model.Movie
import java.util.Comparator

data class MovieBitmap(val movie: Movie, val bitmap: Bitmap) : Parcelable {

    constructor(source: Parcel) : this(
        source.readSerializable() as Movie,
        source.readParcelable<Bitmap>(Bitmap::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(movie)
        writeParcelable(bitmap, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MovieBitmap> = object : Parcelable.Creator<MovieBitmap> {
            override fun createFromParcel(source: Parcel): MovieBitmap = MovieBitmap(source)
            override fun newArray(size: Int): Array<MovieBitmap?> = arrayOfNulls(size)
        }
    }
}


class ImageAdapter(val context: Context, val movieBitmaps: List<MovieBitmap>) : BaseAdapter() {

    val LOG_TAG = this.javaClass.name
    
    val r = Resources.getSystem()
    val pxWidth = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f, r.getDisplayMetrics())).toInt()
    val pxHeight = (pxWidth * 1.5).toInt()
    var comparator: MovieComparator = MovieComparator.BY_DATE_ASC

    var filteredBitmaps: List<MovieBitmap> = movieBitmaps.toMutableList()

    fun filterByGenre(genreFilter: String) {
        filteredBitmaps = if (genreFilter == ALL_GENRES) {
            movieBitmaps.toList()
        } else {
            movieBitmaps.filter { it.movie.genres.contains(genreFilter) }.toList()
        }
    }

    override fun getCount(): Int = filteredBitmaps.size

    override fun getItem(position: Int): Any? = filteredBitmaps[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(this.context)
            imageView.layoutParams = ViewGroup.LayoutParams(pxWidth, pxHeight)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 4, 16)
        }
        else {
            imageView = convertView as ImageView
        }

        imageView.setImageBitmap(this.filteredBitmaps[position].bitmap)
        return imageView
    }

    fun setTitleComparator() {
        comparator = if (comparator === MovieComparator.BY_TITLE_ASC) {
            MovieComparator.BY_TITLE_DESC
        }
        else {
            MovieComparator.BY_TITLE_ASC
        }
        // in place sort
        java.util.Collections.sort(filteredBitmaps, comparator)
    }

    fun setDateComparator() {
        comparator = if (comparator === MovieComparator.BY_DATE_ASC) {
            MovieComparator.BY_DATE_DESC
        }
        else {
            MovieComparator.BY_DATE_ASC
        }
        // in place sort
        java.util.Collections.sort(filteredBitmaps, comparator)
    }
}


enum class MovieComparator : Comparator<MovieBitmap> {
    BY_TITLE_ASC {
        override fun compare(m1: MovieBitmap, m2: MovieBitmap): Int {
            return m1.movie.title.compareTo(m2.movie.title)
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