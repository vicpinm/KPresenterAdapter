package com.vicpin.kpresenteradapter

import android.support.annotation.LayoutRes
import com.vicpin.kpresenteradapter.model.ViewInfo
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
class SimplePresenterAdapter<T: Any>(val viewHolderClass: KClass<out ViewHolder<T>>, @LayoutRes val layoutResourceId: Int) : PresenterAdapter<T>(){

    override fun getViewInfo(position: Int) = ViewInfo(viewHolderClass, layoutResourceId)
}
