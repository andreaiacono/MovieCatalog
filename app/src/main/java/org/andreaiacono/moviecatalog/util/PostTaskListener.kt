package org.andreaiacono.moviecatalog.util

interface PostTaskListener<K> {
    fun onPostTask(result: K, exception: Exception?)
}