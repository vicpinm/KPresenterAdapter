package com.vicpin.kpresenteradapter.viewholder

import android.content.Context
import android.view.View
import com.vicpin.kpresenteradapter.R
import com.vicpin.kpresenteradapter.ViewHolder
import com.vicpin.kpresenteradapter.extensions.inflate

/**
 * Created by Victor on 01/11/2016.
 */
class LoadMoreViewHolder<T: Any> private constructor(override val containerView: View): ViewHolder<T>(containerView){

    override val presenter = null

    companion object{
        fun <T: Any> getInstance(context: Context, layout: Int = R.layout.adapter_load_more) : LoadMoreViewHolder<T>{
            return LoadMoreViewHolder(context.inflate(layout))
        }
    }

}