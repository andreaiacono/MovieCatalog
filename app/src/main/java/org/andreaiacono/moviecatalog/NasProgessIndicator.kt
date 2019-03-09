//package org.andreaiacono.moviecatalog
//
//import android.content.Context
//import android.os.Message
//import java.util.ArrayList
//
//class Mede8erScannerProgressIndicator(context: Context) {
//
//    private var initialized: Boolean = false
//    private var scanStep: Int = 0
//    private var elementCounter: Int = 0
//    private var jukeboxCounter: Int = 0
//    private var totalElements: Int = 0
//    private var parsedElements: Int = 0
//
//    val text = "Scanning NAS..."
//
//    @Throws(Exception::class)
//    override operator fun next(): Int {
//
//        try {
//            return scanJukeboxes()
//        } catch (e: StatusException) {
//            if (e.getStatus() === Mede8erStatus.NO_JUKEBOX) {
//                val dialogHandler = (context as MainActivity).getDialogHandler()
//                dialogHandler.sendMessage(Message.obtain(dialogHandler, NO_JUKEBOX))
//            }
//            return 100
//        }
//
//    }
//
//    @Throws(Exception::class)
//    private fun scanJukeboxes(): Int {
//
//        when (scanStep) {
//
//        }
//
//        return 100
//    }
//
//    @Throws(Exception::class)
//    fun finish() {
//
//        Logger.log("Mede8erScanner finished. Now saving movie file and creating page.")
//        mede8erCommander!!.getMoviesCatalog().setJukeboxes(jukeboxes)
//        mede8erCommander!!.getMoviesCatalog().save()
//        mede8erCommander!!.getMoviesCatalog().sortMovies()
//        mede8erCommander!!.getMoviesCatalog().sortGenres()
//
//        (context as MainActivity).getDialogHandler()
//            .sendMessage(Message.obtain((context as MainActivity).getDialogHandler(), FULLY_OPERATIONAL))
//    }
//
//}
