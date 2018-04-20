package com.vicpin.kpresenteradapter

import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.kpresenteradapter.extensions.refreshData
import com.vicpin.kpresenteradapter.model.ViewInfo
import com.vicpin.kpresenteradapter.viewholder.LoadMoreViewHolder
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
abstract class PresenterAdapter<T : Any>() : RecyclerView.Adapter<ViewHolder<T>>() {

    /**
     * Data collections
     */
    private val registeredViewInfo = arrayListOf<ViewInfo<T>>()
    private val headers = arrayListOf<ViewInfo<T>>()
    private var data = mutableListOf<T>()

    /**
     * Event listeners
     */
    var itemClickListener: ((item: T, view: ViewHolder<T>) -> Unit)? = null
    var itemLongClickListener: ((item: T, view: ViewHolder<T>) -> Unit)? = null
    var loadMoreListener: (() -> Unit)? = null

    /**
     * Sets a custom listener instance. You can call to the listener from your ViewHolder classes with getCustomListener() method.
     * @param customListener
     */
    var customListener: Any? = null

    /**
     * Load more properties
     */
    private var loadMoreEnabled: Boolean = false
    private var loadMoreInvoked: Boolean = false
    private val LOAD_MORE_TYPE = 99999
    private val HEADER_TYPE = 100000
    private var HEADER_MAX_TYPE = HEADER_TYPE

    constructor(data: MutableList<T>) : this() {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T>? {
        if (viewType == LOAD_MORE_TYPE) {
            return LoadMoreViewHolder.getInstance(parent.context)
        } else {
            var viewInfo = getViewInfoForType(viewType)
            return getViewHolder(parent, viewInfo)
        }
    }


    fun getViewInfoForType(viewType: Int) =
            if (isHeaderType(viewType)) headers[viewType - HEADER_TYPE]
            else registeredViewInfo[viewType]


    private fun isHeaderType(viewType: Int) = viewType >= HEADER_TYPE && viewType < HEADER_MAX_TYPE

    private fun getViewHolder(parent: ViewGroup, viewInfo: ViewInfo<T>) : ViewHolder<T>? {
        val view = viewInfo.view ?: if(viewInfo.viewResourceId != null) {
            parent.inflate(viewInfo.viewResourceId!!)
        } else throw IllegalArgumentException("Either viewResourceId or view arguments must be provided to viewInfo class")

        val viewHolder = viewInfo.createViewHolder(view)
        viewHolder?.customListener = customListener
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadMorePosition(position) -> LOAD_MORE_TYPE
            isHeaderPosition(position) -> HEADER_TYPE + position
            else -> getTypeForViewHolder(getViewInfo(position))
        }
    }

    private fun getTypeForViewHolder(viewInfo: ViewInfo<T>) : Int{
        if(!registeredViewInfo.contains(viewInfo)){
            registeredViewInfo.add(viewInfo)
        }
        return registeredViewInfo.indexOf(viewInfo)
    }

    /**
     * Called for each adapter position to get the associated ViewInfo object.
     * ViewInfo class holds the ViewHolder class and the associated layout for building the view
     * @param position item position in the adapter items collection
     * *
     * @return new instance of ViewInfo object
     */
    abstract fun getViewInfo(position: Int): ViewInfo<T>

    private fun isLoadMorePosition(position: Int) = loadMoreEnabled && itemCount - 1 == position

    private fun isHeaderPosition(position: Int) = position < headers.size

    private fun isNormalPosition(position: Int) = !isLoadMorePosition(position) && !isHeaderPosition(position)

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        if(isNormalPosition(position)) {
            holder.onBind(data, getPositionWithoutHeaders(position), deleteListener = {
                removeItem(it)
            })
            appendListeners(holder)
        }
        else if(isHeaderPosition(position)){
            holder.onBindHeader(data)
        }
        else if(isLoadMorePosition(position)){
            notifyLoadMoreReached()
        }
    }

    fun getPositionWithoutHeaders(position: Int) = position - headers.size


    fun getPositionWithHeaders(position: Int) = position + headers.size


    private fun notifyLoadMoreReached() {
        if (loadMoreListener != null && !loadMoreInvoked) {
            loadMoreInvoked = true
            loadMoreListener?.invoke()
        }
    }

    private fun appendListeners(viewHolder: ViewHolder<T>){
        if(itemClickListener != null){
            viewHolder.itemView.setOnClickListener { itemClickListener?.invoke(getItem(viewHolder.adapterPosition), viewHolder) }
        }

        if(itemLongClickListener != null){
            viewHolder.itemView.setOnLongClickListener { itemLongClickListener?.invoke(getItem(viewHolder.adapterPosition), viewHolder); true }
        }
    }

    override fun onViewRecycled(holder: ViewHolder<T>) {
        super.onViewRecycled(holder)
        holder.onDestroy()
    }

    override fun onFailedToRecycleView(holder: ViewHolder<T>): Boolean {
        holder.onDestroy()
        return super.onFailedToRecycleView(holder)
    }

    fun getItem(position: Int) = data[getPositionWithoutHeaders(position)]


    fun addHeader(@LayoutRes layout: Int, viewHolderClass: KClass<out ViewHolder<T>>? = null){
        this.headers.add(ViewInfo(viewHolderClass, layout))
        HEADER_MAX_TYPE = HEADER_TYPE + headers.size
    }

    fun updateHeaders(){
        if(this.headers.size > 0){
            notifyItemRangeChanged(0, headers.size)
        }
    }

    /**
     * Set adapter data and notifies the change
     * @param data items collection
     * @return PresenterAdapter called instance
     */
    fun setData(data: List<T>) : PresenterAdapter<T>{
        this.data = data.toMutableList()
        this.loadMoreInvoked = false
        notifyDataSetChanged()
        return this
    }

    /**
     * Set adapter data and notifies the change, keeping scroll position
     * @param data items collection
     * @param recyclerView RecyclerView instance
     * @return PresenterAdapter called instance
     */
    fun setDataKeepScroll(data: MutableList<T>, recyclerView: RecyclerView){
        this.data = data
        this.loadMoreInvoked = false
        refreshData(recyclerView)
    }

    override fun getItemCount() = data.size + headers.size + if(loadMoreEnabled) 1 else 0

    fun getHeadersCount() : Int = headers.size


    /**
     * Add data at the end of the current data list and notifies the change
     * @param data items collection to append at the end of the current collection
     * @return PresenterAdapter called instance
     */
    fun addData(data: List<T>){

        this.loadMoreInvoked = false
        val currentItemCount = itemCount
        this.data.addAll(data)
        val dataSize = data.size

        Handler().post {
            if(loadMoreEnabled){
                notifyItemChanged(currentItemCount - 1)
            }
            else{
                notifyItemRangeInserted(currentItemCount, dataSize)
            }
        }
    }

    fun clearData(){
        this.data.clear()
        notifyDataSetChanged()
    }

    fun removeItem(item: T){
        if(this.data.contains(item)){
            val pos = this.data.indexOf(item)
            this.data.remove(item)
            notifyItemRemoved(getPositionWithHeaders(pos))
        }
    }

    fun removeItem(position: Int) {
        if(position < this.data.size) {
            this.data.removeAt(position)
            notifyItemRemoved(getPositionWithHeaders(position))
        }
    }

    /**
     * Swap two no header items
     * @param from
     * *
     * @param to
     */
    fun swapItems(from: Int, to: Int) {
        var from = from
        var to = to
        if (isHeaderPosition(from) || isHeaderPosition(to)) {
            throw IllegalArgumentException("Header positions are not swapable")
        }

        from -= getHeadersCount()
        to -= getHeadersCount()

        if (from >= data.size || to >= data.size) {
            throw IllegalArgumentException("Cannot swap items, data size is " + data.size)
        }

        if (from == to) {
            return
        }

        Collections.swap(data, from, to)

        notifyItemMoved(from, to)

    }

    /**
     * Move one item to another position, updating intermediates positions
     * @param from
     * *
     * @param to
     */
    fun moveItem(from: Int, to: Int) {
        var from = from
        var to = to
        if (isHeaderPosition(from) || isHeaderPosition(to)) {
            throw IllegalArgumentException("Header positions are not swapable")
        }

        from -= getHeadersCount()
        to -= getHeadersCount()

        if (from >= data.size || to >= data.size) {
            throw IllegalArgumentException("Cannot move item, data size is " + data.size)
        }

        if (from == to) {
            return
        }

        val item = data.removeAt(from)
        data.add(to, item)

        notifyItemMoved(from, to)

    }

    /**
     * Enable load more option for paginated collections
     * @param loadMoreListener
     */
    fun enableLoadMore(loadMoreListener:  (() -> Unit)?) {
        this.loadMoreEnabled = true
        this.loadMoreInvoked = false
        this.loadMoreListener = loadMoreListener
        notifyItemInserted(itemCount)
    }

    /**
     * Disable load more option
     */
    fun disableLoadMore() {
        if(this.loadMoreEnabled) {
            this.loadMoreEnabled = false
            this.loadMoreInvoked = false
            notifyItemRemoved(itemCount)
        }
    }

    fun isLoadMoreEnabled() = loadMoreEnabled

    override fun getItemId(position: Int): Long {
        if(isLoadMorePosition(position)){
            return LOAD_MORE_TYPE.toLong()
        }
        else if (hasStableIds()) {
            return if (position < getHeadersCount()) headers[position].hashCode().toLong() else getItem(position).hashCode().toLong()
        } else {
            return super.getItemId(position)
        }
    }

    fun getData() = data


}