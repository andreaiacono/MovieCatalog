package org.andreaiacono.moviecatalog

interface PostTaskListener<K> {
    fun onPostTask(result: K, exception: Exception?)
}