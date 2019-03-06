package org.andreaiacono.moviecatalog.activity

import android.graphics.Bitmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import android.widget.Toast
import org.andreaiacono.moviecatalog.R
import org.andreaiacono.moviecatalog.network.RetrieveImagesTask
import java.util.logging.Logger
import org.andreaiacono.moviecatalog.util.ImageAdapter
import org.andreaiacono.moviecatalog.util.PostTaskListener


class MainActivity : PostTaskListener<List<Bitmap>>, AppCompatActivity() {

    private var logger: Logger = Logger.getAnonymousLogger()

    override fun onPostTask(result: List<Bitmap>, exception: Exception?) {
        if (exception != null) {
            val toast = Toast.makeText(applicationContext, "An error occurred while retrieving the images from network: ${exception.message}", Toast.LENGTH_LONG)
            toast.show()
        }
        else {
            this.imageGrid!!.setAdapter(ImageAdapter(this, result))
        }
    }

    private var imageGrid: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid)
//        setSupportActionBar(toolbar)
        this.imageGrid = findViewById<GridView>(R.id.gridview)
        RetrieveImagesTask(this).execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
