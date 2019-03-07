package org.andreaiacono.moviecatalog.util

import android.content.Context
import java.io.File
import java.util.*

private fun deleteData(ctx: Context) {
    val thumbsDir = ctx.getFilesDir().absolutePath + "/thumb"
    File(thumbsDir).listFiles().forEach { it.delete() }
    ctx.getFilesDir().listFiles().forEach { it.delete() }
}

fun save(ctx: Context) {

//    val fileContent = StringBuilder()
//
//    // separates jukeboxes from genres/movies
//    fileContent.append("\n")
//    Collections.sort<String>(genres)
//    val genresArray = arrayOfNulls<String>(genres.size)
//    val genresValues = Arrays.toString(genres.toTypedArray())
//    fileContent.append(genresValues, 1, genresValues.length - 1).append("\n")
//    for (movie in movies) {
//        fileContent.append(movie.toDataFormat())
//    }
//    Logger.log("saving " + Constants.MOVIES_FILE) // + ": " + fileContent.toString());
//
//    val outputStream = ctx.openFileOutput(Constants.MOVIES_FILE, Context.MODE_PRIVATE)
//    outputStream.write(fileContent.toString().toByteArray())
//    outputStream.close()
}