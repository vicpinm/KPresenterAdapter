package com.vicpin.kpresenteradapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Victor on 01/11/2016.
 */
abstract class ViewHolder<in T: Any> : androidx.recyclerview.widget.RecyclerView.ViewHolder, LayoutContainer {

    abstract val presenter: ViewHolderPresenter<in T, *>?
    val context: Context
    var customListener: Any? = null

    override val containerView: View?
        get() = itemView

    constructor(itemView: View) : super(itemView){
        context = itemView.context
    }

    fun onBind(data: List<T>, position: Int, deleteListener: () -> Unit){
        setupPresenter(data, deleteListener)
        presenter?.data = data[position]
        presenter?.onCreate()
    }


    /**
     * Called when adapter's onBindViewHolder is executed for a header row type
     * Initializes presenter binding view without data item
     */
    fun onBindHeader(data: List<T>) {
        setupPresenter(data)
        presenter?.onCreate()
    }

    fun onAttached() {
        presenter?.onAttached()
    }

    fun onDetached() {
        presenter?.onDetached()
    }


    private fun setupPresenter(data: List<T>, listener: (() -> Unit)? = null){
        presenter?.setPresenterView(this)
        presenter?.dataCollection = data
        presenter?.onDeleteListener = listener
    }

    fun onScrollStopped() {
        presenter?.onScrollStoped()
    }

    fun onDestroy(){
        presenter?.onPreDestroy()
    }
}