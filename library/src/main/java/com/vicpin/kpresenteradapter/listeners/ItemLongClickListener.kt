package com.vicpin.kpresenteradapter.listeners

import com.vicpin.kpresenteradapter.ViewHolder

/**
 * Created by Victor on 01/11/2016.
 */
interface ItemLongClickListener<T: Any> {

    fun onItemLongClick(item: T, view: ViewHolder<T>)
}