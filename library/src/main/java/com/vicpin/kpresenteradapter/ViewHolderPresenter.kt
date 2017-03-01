package com.vicpin.kpresenteradapter

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
    val presenterId: Int by lazy { presenterIdsGenerator.andIncrement }


    fun setPresenterView(view: Any){
        this.view = view as? PresenterView
    }
    /**
     * Called when the view becomes visible in the adapter
     */
    abstract fun onCreate()

    /**
     * Called when the view is recycled and is no more visible in the adapter
     */
    open fun onDestroy(){}
}