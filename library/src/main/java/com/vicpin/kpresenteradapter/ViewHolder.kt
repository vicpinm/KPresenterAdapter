package com.vicpin.kpresenteradapter

import android.content.Context
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Victor on 01/11/2016.
 */
abstract class ViewHolder<T: Any> : RecyclerView.ViewHolder, LayoutContainer {

    abstract val presenter: ViewHolderPresenter<T, *>?
    val context: Context
    var customListener: Any? = null

    override val containerView: View?
        get() = itemView

    constructor(itemView: View) : super(itemView){
        context = itemView.context
    }

    fun <A: T> onBind(data: List<A>, position: Int, scrollState: Int, deleteListener: () -> Unit){
        setupPresenter(data, deleteListener, scrollState)
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
        presenter?.onAttach()
    }

    fun onDetached() {
        presenter?.onDetach()
    }


    private fun <A: T> setupPresenter(data: List<A>, listener: (() -> Unit)? = null, scrollState: Int = 0){
        presenter?.apply {
            setPresenterView(this@ViewHolder)
            dataCollection = data
            onDeleteListener = listener
            this.scrollState = scrollState
        }
    }

    fun onScrollStopped() {
        presenter?.onScrollStoped()
    }

    fun onDestroy(){
        presenter?.onPreDestroy()
    }

    fun setScrollState(state: Int) {
        presenter?.scrollState = state
    }
}