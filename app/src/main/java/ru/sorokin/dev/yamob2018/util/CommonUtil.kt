package ru.sorokin.dev.yamob2018.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import io.realm.RealmResults
import io.realm.RealmChangeListener
import android.arch.lifecycle.LiveData
import com.bumptech.glide.annotation.GlideModule
import io.realm.RealmModel
import com.bumptech.glide.module.AppGlideModule
import io.realm.kotlin.addChangeListener
import io.realm.kotlin.removeChangeListener


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

class RealmSingleLiveData<T : RealmModel>(private val result: T) : LiveData<T>() {
    private val listener = RealmChangeListener<T> { res -> value = res }
    override fun onActive() {
        result.addChangeListener(listener)
    }

    override fun onInactive() {
        result.removeChangeListener(listener)
    }
}

@GlideModule
class MyAppGlideModule : AppGlideModule()

object ConvertUtils{
    val BYTES_IN_ONE_GB = 1073741824

    fun bytesToGbs(bytes: Long): Double{
        return bytes.toDouble() / BYTES_IN_ONE_GB
    }
}