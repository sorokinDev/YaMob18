package ru.sorokin.dev.yamob2018.util

import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View

class BottomNavigationViewBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    private var height: Int = 0
    var bottomVis: Boolean = true

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

        //Log.i("Beh", "onScr")
        if (dyConsumed > 0) {
            slideDown(child)
        } else if (dyConsumed < 0) {
            slideUp(child)
        }
    }

    private fun slideUp(child: BottomNavigationView) {
        if(!bottomVis){
            bottomVis = true
            child.clearAnimation()
            child.animate().translationY(0f).duration = 150
        }
    }

    private fun slideDown(child: BottomNavigationView) {
        if(bottomVis){
            bottomVis = false
            child.clearAnimation()
            child.animate().translationY(height.toFloat()).duration = 150
        }
    }
}