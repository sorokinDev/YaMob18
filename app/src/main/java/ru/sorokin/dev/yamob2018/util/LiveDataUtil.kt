package ru.sorokin.dev.yamob2018.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

fun <T> mutableLiveDataWithValue(v: T): MutableLiveData<T> = MutableLiveData<T>().apply { value = v }

fun <T> LiveData<T>.observe(lifeCicleOwner: LifecycleOwner, observer: (value: T?) -> Unit) {
    this.observe(lifeCicleOwner, android.arch.lifecycle.Observer { observer(it) })
}

class RealmLiveData<T : RealmModel>(private val results: RealmResults<T>) : LiveData<RealmResults<T>>() {
    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }
    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }
}

fun <T: RealmModel> RealmResults<T>.asLiveData(): RealmLiveData<T>{
    return RealmLiveData(this)
}