package com.vicpin.kpresenteradapter.model

import android.view.View
import com.vicpin.kpresenteradapter.ViewHolder
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
data class ViewInfo<in T: Any>(val viewHolderClass: KClass<out ViewHolder<T>>? = null) {

    var viewResourceId: Int? = null
    var view: View? = null


    constructor(viewHolderClass: KClass<out ViewHolder<T>>? = null, viewResourceId: Int): this(viewHolderClass) {
        this.viewResourceId = viewResourceId
    }

    constructor(viewHolderClass: KClass<out ViewHolder<T>>? = null, view: View): this(viewHolderClass) {
        this.view = view
    }

    fun createViewHolder(view: View): ViewHolder<T>? {
        return if (viewHolderClass != null) {
            try {
                viewHolderClass.java.getConstructor(View::class.java).newInstance(view)
            } catch (ex: Exception) {
                ex.printStackTrace(); null
            }
        } else {
            object : ViewHolder<T>(view) {
                override val containerView = view
                override val presenter = null
            }

        }
    }

}




