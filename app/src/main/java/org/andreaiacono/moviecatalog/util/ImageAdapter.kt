package org.andreaiacono.moviecatalog.util

import android.widget.*
import android.graphics.*
import android.view.*
import android.content.*
import android.content.res.Resources
import android.widget.ImageView.ScaleType
import android.widget.GridView
import android.util.TypedValue
import android.view.ViewGroup



class ImageAdapter(val context: Context, val bitmapList: List<Bitmap>) : BaseAdapter() {

    override fun getCount(): Int = bitmapList.size

    override fun getItem(position: Int): Any? = bitmapList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {

            //Calculation of ImageView Size - density independent.
            //maybe you should do this calculation not exactly in this method but put is somewhere else.
            val r = Resources.getSystem()
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, r.getDisplayMetrics())

            imageView = ImageView(this.context)
            imageView.layoutParams = ViewGroup.LayoutParams(px.toInt(), px.toInt())
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 4, 16)
        }
        else {
            imageView = convertView as ImageView
        }

        imageView.setImageBitmap(this.bitmapList[position])
        return imageView
    }
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val imageView: ImageView
//        if (convertView == null) {
//            imageView = ImageView(this.context)
//            imageView.layoutParams = ViewGroup.LayoutParams(150, 150)
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        } else {
//            imageView = convertView as ImageView
//        }
//
//        imageView.setImageBitmap(this.bitmapList[position])
//        return imageView
//    }

}