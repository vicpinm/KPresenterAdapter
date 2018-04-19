package com.vicpin.kpresenteradapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Victor on 01/11/2016.
 */
abstract class ViewHolder<in T: Any> : RecyclerView.ViewHolder, LayoutContainer {

    abstract val presenter: ViewHolderPresenter<in T, *>?
    val context: Context
    var customListener: Any? = null

    override val containerView: View?
        get() = itemView

    constructor(itemView: View) : super(itemView){
        context = itemView.context
    }

    fun onBind(data: List<T>, position: Int, deleteListener: (Int) -> Unit){
        setupPresenter(data, deleteListener)
        presenter?.data = data[position]
        presenter?.position = position
        presenter?.onCreate()
    }

    /**
     * Called when adapter's onBindViewHolder is executed for a header row type
     * Initializes presenter binding view but no data item
     */
    fun onBindHeader(data: List<T>) {
        setupPresenter(data)
        presenter?.onCreate()
    }


    private fun setupPresenter(data: List<T>, listener: ((Int) -> Unit)? = null){
        presenter?.setPresenterView(this)
        presenter?.dataCollection = data
        presenter?.setDeleteListener = listener
    }


    fun onDestroy(){
        presenter?.onPreDestroy()
    }
}