package com.vicpin.kpresenteradapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Victor on 01/11/2016.
 */
abstract class ViewHolder<T: Any> : RecyclerView.ViewHolder {

    abstract val presenter: ViewHolderPresenter<T, *>?
    val context: Context
    var customListener: Any? = null


    constructor(itemView: View) : super(itemView){
        context = itemView.context
    }

    fun onBind(data: List<T>, position: Int, deleteListener: (T) -> Unit){
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


    private fun setupPresenter(data: List<T>, listener: ((T) -> Unit)? = null){
        presenter?.setPresenterView(this)
        presenter?.dataCollection = data
        presenter?.setDeleteListener = listener
    }


    fun onDestroy(){
        presenter?.onPreDestroy()
    }
}