package org.andreaiacono.moviecatalog.util

import android.widget.*
import android.graphics.*
import android.view.*
import android.content.*
import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewGroup



class ImageAdapter(val context: Context, val bitmapList: List<Bitmap>) : BaseAdapter() {

    val r = Resources.getSystem()
    val pxWidth = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, r.getDisplayMetrics())).toInt()
    val pxHeight = (pxWidth * 1.5).toInt()

    override fun getCount(): Int = bitmapList.size

    override fun getItem(position: Int): Any? = bitmapList[position]

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

        imageView.setImageBitmap(this.bitmapList[position])
        return imageView
    }
}