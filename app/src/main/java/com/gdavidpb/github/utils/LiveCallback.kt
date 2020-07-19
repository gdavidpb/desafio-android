package com.gdavidpb.github.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveCallback<T>(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
    private val liveData = MutableLiveData<T>()

    init {
        liveData.observe(lifecycleOwner, Observer(callback))
    }

    fun postCallback(value: T) {
        liveData.postValue(value)
    }
}