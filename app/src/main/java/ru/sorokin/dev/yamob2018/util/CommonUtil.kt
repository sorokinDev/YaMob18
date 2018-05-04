package ru.sorokin.dev.yamob2018.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults
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
    const val BYTES_IN_ONE_GB = 1073741824

    fun bytesToGbs(bytes: Long): Double{
        return bytes.toDouble() / BYTES_IN_ONE_GB
    }
}

class BottomNavigationViewBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    private var height: Int = 0

    override fun onLayoutChild(parent: CoordinatorLayout?, child: BottomNavigationView?, layoutDirection: Int): Boolean {
        height = child!!.height
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: BottomNavigationView, directTargetChild: View, target: View,
                                     axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int,
                                @ViewCompat.NestedScrollType type: Int) {
        if (dyConsumed > 0) {
            slideDown(child)
        } else if (dyConsumed < 0) {
            slideUp(child)
        }
    }

    private fun slideUp(child: BottomNavigationView) {
        child.clearAnimation()
        child.animate().translationY(0f).duration = 200
    }

    private fun slideDown(child: BottomNavigationView) {
        child.clearAnimation()
        child.animate().translationY(height.toFloat()).duration = 200
    }
}

