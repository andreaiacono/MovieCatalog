package org.andreaiacono.moviecatalog.util

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.GridView
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.service.MoviesCatalog


class GridViewMultiSelector(val gridView: GridView, val moviesCatalog: MoviesCatalog) : AbsListView.MultiChoiceModeListener {

    val LOG_TAG = this.javaClass.name

    override fun onActionItemClicked(mode: android.view.ActionMode?, menu: MenuItem?): Boolean {
        val adapter = gridView.adapter as ImageAdapter
        val checked = gridView.checkedItemPositions
        val size =  gridView.count
        for (i in 0 until size) {
            if (checked.get(i)) {
                val item = adapter.getItem(i) as MovieBitmap
                moviesCatalog.findByNasDirname(item.movie.nasDirName)?.seen = true
                Log.d(LOG_TAG, "Setting as seen [${item.movie.title}]")
            }
        }
        moviesCatalog.saveCatalog()
//        adapter.movieBitmaps.forEach { it.movie.selected = false }
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

//        (gridView.adapter as ImageAdapter).movieBitmaps[position].movie.selected = true
    }

    override fun onDestroyActionMode(mode: android.view.ActionMode?) {
        val adapter = gridView.adapter as ImageAdapter
//        adapter.movieBitmaps.forEach { it.movie.selected = false }
        adapter.notifyDataSetChanged()

    }
}