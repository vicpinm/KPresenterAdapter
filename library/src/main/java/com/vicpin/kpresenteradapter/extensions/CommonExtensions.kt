package com.vicpin.kpresenteradapter.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vicpin.kpresenteradapter.ViewHolder

/**
 * Created by Victor on 01/11/2016.
 */

fun Context.inflate(layout: Int, root: ViewGroup? = null, attachToParent: Boolean = false): View {
    return LayoutInflater.from(this).inflate(layout, root, attachToParent)
}

fun ViewGroup.inflate(layout: Int, attachToParent: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToParent)
}

fun RecyclerView.getCurrentScroll(): Pair<Int, Int> {
    val currentPosition = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
    val top = if (getChildAt(0) == null) 0 else getChildAt(0).top - paddingTop
    return Pair(currentPosition ?: 0, top)
}

fun RecyclerView.applyScroll(position: Int, top: Int) {
    (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, top)
}


fun <T : Any> RecyclerView.Adapter<out ViewHolder<T>>.refreshData(recyclerView: RecyclerView) {
    var (position, top) = recyclerView.getCurrentScroll()
    notifyDataSetChanged()
    recyclerView.applyScroll(position, top)
}

/**
 * Reload current views showing on the screen
 * @param recyclerView
 */
fun RecyclerView.forEachVisibleView(doOnEach: (Int) -> Unit) {

    if(layoutManager is LinearLayoutManager) {
        val firstPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        for (i in firstPosition..lastPosition) {
            doOnEach(i)
        }
    }
}
