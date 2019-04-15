package org.andreaiacono.moviecatalog.util

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.GridView
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.activity.MainActivity
import org.andreaiacono.moviecatalog.model.Movie
import org.andreaiacono.moviecatalog.service.MoviesCatalog


class GridViewMultiSelector(val mainActivity: MainActivity, val gridView: GridView, val moviesCatalog: MoviesCatalog) : AbsListView.MultiChoiceModeListener {

    val LOG_TAG = this.javaClass.name

    override fun onActionItemClicked(mode: android.view.ActionMode?, menu: MenuItem?): Boolean {
        val adapter = gridView.adapter as ImageAdapter
        val checked = gridView.checkedItemPositions
        val size =  gridView.count
        val movies = mutableListOf<Movie>()
        for (i in 0 until size) {
            if (checked.get(i)) {
                val item = adapter.getItem(i) as MovieBitmap
                // FIXME: this call can be removed and use only the `item` val
                val movie = moviesCatalog.findByNasDirname(item.movie.nasDirName)
                if (movie != null) {
                    Log.d(LOG_TAG, "Setting as seen [${item.movie.title}]")
                    movies.add(movie)
                }
            }
        }
        moviesCatalog.setAsSeen(movies, mainActivity)
        adapter.filteredBitmaps.forEach { it.selected = !it.selected }
        adapter.notifyDataSetChanged()
        mode?.finish()
        return true
    }

    override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_mark_as_read, menu)
        mode?.title = "Select movies"
        mode?.subtitle = "1 movie selected"
        return true
    }

    override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onItemCheckedStateChanged(mode: android.view.ActionMode?, position: Int, id: Long, checked: Boolean) {
        when (val selectCount = gridView.checkedItemCount) {
            1 -> mode!!.subtitle = "1 movie selected"
            else -> mode!!.subtitle = "$selectCount movies selected"
        }
        val adapter = gridView.adapter as ImageAdapter
        val movieBitmap = adapter.getItem(position) as MovieBitmap
        movieBitmap.selected = !movieBitmap.selected
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyActionMode(mode: android.view.ActionMode?) {
        val adapter = gridView.adapter as ImageAdapter
        adapter.filteredBitmaps.forEach { it.selected = false }
        adapter.notifyDataSetChanged()
    }
}