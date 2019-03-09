package org.andreaiacono.moviecatalog

import android.content.Context

abstract class GenericProgressIndicator(val context: Context) {

    private var counter: Int = 0

    /**
     * returns the max for the progress bar
     * @return
     */
    var max = 100


    lateinit var genres: List<String>

    val text: CharSequence
        get() = "Message for item $counter"


    /**
     * setups the indicator
     * @return true if setup was ok and process has to continue; false if process has not to go on.
     * @throws Exception
     */
    fun setup(): Boolean {
        counter = 0
        return true
    }


    /**
     * processes the next element of the set
     * @return
     * @throws Exception
     */
    fun next(): Int {
        return counter++
    }


    /**
     * finishes the execution
     * @throws Exception
     */
    @Throws(Exception::class)
    fun finish() {

    }

    /**
     * something has gone wrong
     */
    fun fail() {

    }
}
