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

    val r = Resources.getSystem()
    val pxWidth = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, r.getDisplayMetrics())).toInt()
    val pxHeight = (pxWidth * 1.5).toInt()

    var filteredBitmaps: List<MovieBitmap> = movieBitmaps.toList()

    fun filterByGenre(genreFilter: String) {
        if (genreFilter == ALL_GENRES) {
            filteredBitmaps = movieBitmaps.toList()
        }
        else {
            filteredBitmaps = movieBitmaps.filter { it.movie.genres.contains(genreFilter) }.toList()
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
}