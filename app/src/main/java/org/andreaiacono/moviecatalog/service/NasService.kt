package org.andreaiacono.moviecatalog.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import jcifs.smb.SmbFile
import java.io.InputStream
import java.io.Serializable
import jcifs.smb.SmbFileOutputStream


class NasService(val url: String) : Serializable {

    val LOG_TAG = this.javaClass.name

    fun getMoviesDirectories(): Array<SmbFile> {
        val moviesRoot = SmbFile(url)
        return moviesRoot.listFiles()
    }

    private fun getUrl(dirName: String, filename: String) = "$url$dirName$filename"

    private fun getFullImage(dirName: String, filename: String) =
        urlImageToBitmap(SmbFile(getUrl(dirName, filename)).inputStream)

    private fun getXmlFileUrl(dirName: String): SmbFile {
        val filename = getUrl(dirName, "${dirName.dropLast(1)}.xml")
        val file = SmbFile(filename)
        return if (file.exists()) {
            file
        } else {
            SmbFile(getUrl(dirName, "info.xml"))
        }
    }

    fun loadXml(dirName: String): String = getXmlFileUrl(dirName).inputStream.readBytes().toString(Charsets.UTF_8)

    fun saveXml(nasDirName: String, xml: String) {
        val fileOutputStream = SmbFileOutputStream(getXmlFileUrl(nasDirName))
        fileOutputStream.write(xml.toByteArray(Charsets.UTF_8))
        fileOutputStream.flush()
    }

    fun getThumbnail(dirName: String): Bitmap = getFullImage(dirName, "folder.jpg")

    fun getFullImage(dirName: String): Bitmap = getFullImage(dirName, "about.jpg")

    private fun urlImageToBitmap(imageStream: InputStream): Bitmap = BitmapFactory.decodeStream(imageStream)
}

