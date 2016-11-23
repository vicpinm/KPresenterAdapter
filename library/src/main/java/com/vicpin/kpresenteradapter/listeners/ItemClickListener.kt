package com.vicpin.kpresenteradapter.listeners

import com.vicpin.kpresenteradapter.ViewHolder

/**
 * Created by Victor on 01/11/2016.
 */
interface ItemClickListener<T: Any> {

    fun onItemClick(item: T, view: ViewHolder<T>)
}