package com.vicpin.kpresenteradapter

import android.content.Context
import android.view.View
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

    var visible: Boolean = false
        set(value) {
            field = value
            presenter?.visible = field
        }

    constructor(itemView: View) : super(itemView){
        context = itemView.context
    }

    fun <A: T> onBind(data: List<A>, position: Int, scrollState: Int,
                      deleteListener: () -> Unit,
                      refreshViewsListener: () -> Unit){
        setupPresenter(data, deleteListener, refreshViewsListener, scrollState)
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
        visible = false
        presenter?.visible = false
        presenter?.onDetach()
    }


    private fun <A: T> setupPresenter(data: List<A>, listener: (() -> Unit)? = null,
                                      updateViewsListener: (() -> Unit)? = null, scrollState: Int = 0){
        presenter?.apply {
            setPresenterView(this@ViewHolder)
            dataCollection = data
            onDeleteListener = listener
            refreshViewsListener = updateViewsListener
            this.scrollState = scrollState
        }
    }

    fun onScrollStopped() {
        presenter?.onScrollStoped()
    }

    fun onShowed() {
        presenter?.onShowed()
    }

    fun onDestroy(){
        presenter?.onPreDestroy()
    }

    fun release(){
        customListener = null
        onDestroy()
    }

    fun setScrollState(state: Int) {
        presenter?.scrollState = state
    }
}