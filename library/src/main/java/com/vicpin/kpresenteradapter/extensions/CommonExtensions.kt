package com.vicpin.kpresenteradapter.extensions

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

fun androidx.recyclerview.widget.RecyclerView.getCurrentScroll(): Pair<Int, Int> {
    val currentPosition = (layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)?.findFirstVisibleItemPosition()
    val top = if (getChildAt(0) == null) 0 else getChildAt(0).top - paddingTop
    return Pair(currentPosition ?: 0, top)
}

fun androidx.recyclerview.widget.RecyclerView.applyScroll(position: Int, top: Int) {
    (getLayoutManager() as? androidx.recyclerview.widget.LinearLayoutManager)?.scrollToPositionWithOffset(position, top)
}


fun <T : Any> androidx.recyclerview.widget.RecyclerView.Adapter<out ViewHolder<T>>.refreshData(recyclerView: androidx.recyclerview.widget.RecyclerView) {
    var (position, top) = recyclerView.getCurrentScroll()
    notifyDataSetChanged()
    recyclerView.applyScroll(position, top)
}

/**
 * Reload current views showing on the screen
 * @param recyclerView
 */
fun <T : Any> androidx.recyclerview.widget.RecyclerView.Adapter<out ViewHolder<T>>.refreshViews(recyclerView: androidx.recyclerview.widget.RecyclerView) {

    if(recyclerView.layoutManager != null) {
        val firstPosition = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
        val lastPosition = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
        for (i in firstPosition..lastPosition) {
            notifyItemChanged(i)
        }
    }
}
