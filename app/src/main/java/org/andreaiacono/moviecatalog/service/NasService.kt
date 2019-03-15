package org.andreaiacono.moviecatalog.service

import jcifs.smb.SmbFile
import org.andreaiacono.moviecatalog.network.NasReader
import java.io.Serializable


class NasService(url: String) : Serializable {

    private val nasReader = NasReader(url)

    fun getMoviesDirectories(): Array<SmbFile> = nasReader.getMoviesDirectories()

    fun getThumbnail(movieDir: String) = nasReader.getThumb(movieDir)

    fun getFullImage(movieDir: String) = nasReader.getFullImage(movieDir)
}

