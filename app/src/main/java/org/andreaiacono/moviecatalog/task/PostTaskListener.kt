package org.andreaiacono.moviecatalog.task

interface PostTaskListener<K> {
    fun onPostTask(result: K, asyncTaskType: AsyncTaskType, exception: Exception?)
}