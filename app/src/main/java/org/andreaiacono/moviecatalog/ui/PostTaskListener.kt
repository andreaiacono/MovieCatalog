package org.andreaiacono.moviecatalog.ui

interface PostTaskListener<K> {
    fun onPostTask(result: K, asyncTaskType: AsyncTaskType, exception: Exception?)
}