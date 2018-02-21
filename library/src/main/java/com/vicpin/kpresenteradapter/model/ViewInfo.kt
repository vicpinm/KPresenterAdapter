package com.vicpin.kpresenteradapter.model

import android.view.View
import com.vicpin.kpresenteradapter.ViewHolder
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
data class ViewInfo<T: Any>(val viewHolderClass: KClass<out ViewHolder<T>>?, val viewResourceId: Int)

internal fun <T: Any> ViewInfo<T>.createViewHolder(view: View) : ViewHolder<T>? {
    return if(viewHolderClass != null) {
        try {
            viewHolderClass.java.getConstructor(View::class.java).newInstance(view)
        } catch (ex: Exception) {
            ex.printStackTrace(); null
        }
    } else {
        object : ViewHolder<T>(view) { override val presenter = null }
    }
}




