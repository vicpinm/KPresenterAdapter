package com.vicpin.kpresenteradapter

import android.widget.AbsListView
import com.vicpinm.autosubscription.Unsubscriber
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Victor on 01/11/2016.
 */
abstract class ViewHolderPresenter<Data : Any, PresenterView: Any> {

    companion object{
        val presenterIdsGenerator: AtomicInteger = AtomicInteger()
    }

    var view: PresenterView? = null
    lateinit var data: Data
    lateinit var dataCollection: List<Data>
    var onDeleteListener: (() -> Unit)? = null
    var scrollState: Int = AbsListView.OnScrollListener.SCROLL_STATE_IDLE

    val presenterId: Int by lazy { presenterIdsGenerator.andIncrement }

    fun setPresenterView(view: Any){
        this.view = view as? PresenterView
    }
    /**
     * Called when this viewholder is binded to the current data item
     */
    abstract fun onCreate()

    /**
     * Called when the view becomes visible to user
     */
    open fun onAttach() {

    }

    /**
     * Called when the view is out of the screen
     */
    open fun onDetach() {

    }

    open fun onScrollStoped() {

    }

    fun onPreDestroy(){
        Unsubscriber.unlink(this)
        onDestroy()
    }

    /**
     * Called when the view is recycled and is ready to be reused
     */
    open fun onDestroy(){}

    fun deleteItemFromCollection() {
        onDeleteListener?.invoke()
    }



}