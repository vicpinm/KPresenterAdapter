package com.vicpin.kpresenteradapter

import androidx.annotation.LayoutRes
import com.vicpin.kpresenteradapter.model.ViewInfo
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
class SimplePresenterAdapter<T: Any>(val viewHolderClass: KClass<out ViewHolder<T>>, @LayoutRes val layoutResourceId: Int) : PresenterAdapter<T>(){

    constructor(viewHolderClass: Class<out ViewHolder<T>>, @LayoutRes layoutResourceId: Int) : this(viewHolderClass.kotlin, layoutResourceId)

    override fun getViewInfo(position: Int) = ViewInfo(viewHolderClass, layoutResourceId)
}
