package com.vicpin.kpresenteradapter

import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.vicpin.kpresenteradapter.extensions.*
import com.vicpin.kpresenteradapter.model.ViewInfo
import com.vicpin.kpresenteradapter.test.Identifable
import com.vicpin.kpresenteradapter.viewholder.LoadMoreViewHolder
import java.util.*
import kotlin.reflect.KClass



/**
 * Created by Victor on 01/11/2016.
 */
abstract class PresenterAdapter<T : Any>() : MyListAdapter<T, ViewHolder<T>>(DiffUtilCallback<T>()) {

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
    var mRecyclerView: RecyclerView? = null

    override fun getRecyclerView(): RecyclerView? {
        return mRecyclerView
    }

    var scrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE

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
    private var HEADER_MAX_TYPE = HEADER_TYPE
    private var enableAnimations = false


    companion object {
        const val LOAD_MORE_TYPE = Int.MAX_VALUE
        const val HEADER_TYPE = Int.MIN_VALUE
    }

    constructor(data: MutableList<T>) : this() {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        if (viewType == LOAD_MORE_TYPE) {
            return LoadMoreViewHolder.getInstance(parent.context)
        } else {
            var viewInfo = getViewInfoForType(viewType)
            return getViewHolder(parent, viewInfo)
        }
    }


    private fun getViewInfoForType(viewType: Int) =
            if (isHeaderType(viewType)) headers[viewType - HEADER_TYPE]
            else registeredViewInfo[viewType]


    private fun isHeaderType(viewType: Int) = viewType >= HEADER_TYPE && viewType < HEADER_MAX_TYPE

    private fun getViewHolder(parent: ViewGroup, viewInfo: ViewInfo<T>): ViewHolder<T> {
        val view = viewInfo.view ?: if (viewInfo.viewResourceId != null) {
            parent.inflate(viewInfo.viewResourceId!!)
        } else throw IllegalArgumentException("Either viewResourceId or view arguments must be provided to viewInfo class")

        val viewHolder = viewInfo.createViewHolder(view)
        viewHolder.customListener = customListener
        return viewHolder as ViewHolder<T>
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadMorePosition(position) -> LOAD_MORE_TYPE
            isHeaderPosition(position) -> HEADER_TYPE + position
            else -> getTypeForViewHolder(getViewInfo(position))
        }
    }

    private fun getTypeForViewHolder(viewInfo: ViewInfo<T>): Int {
        if (!registeredViewInfo.contains(viewInfo)) {
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
        when {
            isNormalPosition(position) -> {
                holder.onBind(data, getPositionWithoutHeaders(position), this@PresenterAdapter.scrollState, deleteListener = {
                    removeItem(getPositionWithoutHeaders(holder.adapterPosition))
                }, refreshViewsListener = {
                    mRecyclerView?.refreshVisibleViews()
                })
                appendListeners(holder)
            }
            isHeaderPosition(position) -> holder.onBindHeader(data)
            isLoadMorePosition(position) -> notifyLoadMoreReached()
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder<T>) {
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder<T>) {
        holder.onDetached()
    }

    fun getPositionWithoutHeaders(position: Int) = position - headers.size


    fun getPositionWithHeaders(position: Int) = position + headers.size


    private fun notifyLoadMoreReached() {
        if (loadMoreListener != null && !loadMoreInvoked) {
            loadMoreInvoked = true
            loadMoreListener?.invoke()
        }
    }

    private fun appendListeners(viewHolder: ViewHolder<T>) {
        if (itemClickListener != null) {
            viewHolder.itemView.setOnClickListener { itemClickListener?.invoke(getItem(viewHolder.adapterPosition), viewHolder) }
        }

        if (itemLongClickListener != null) {
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

    override fun getItem(position: Int) = data[getPositionWithoutHeaders(position)]


    fun addHeader(@LayoutRes layout: Int, viewHolderClass: KClass<out ViewHolder<T>>? = null) {
        this.headers.add(ViewInfo(viewHolderClass, layout))
        HEADER_MAX_TYPE = HEADER_TYPE + headers.size
    }

    fun updateHeaders() {
        if (this.headers.size > 0) {
            notifyItemRangeChanged(0, headers.size)
        }
    }

    /**
     * Set adapter data and notifies the change
     * @param data items collection
     * @return PresenterAdapter called instance
     */
    fun setData(data: List<T>): PresenterAdapter<T> {
        this.data = data.toMutableList()
        this.loadMoreInvoked = false
        if (enableAnimations) {

            submitList(data)

        } else {
            notifyDataSetChanged()
        }
        return this
    }

    /**
     * Set adapter data and notifies the change, keeping scroll position
     * @param data items collection
     * @param recyclerView RecyclerView instance
     * @return PresenterAdapter called instance
     */
    fun setDataKeepScroll(data: MutableList<T>, recyclerView: RecyclerView) {
        this.data = data
        this.loadMoreInvoked = false
        refreshData(recyclerView)
    }

    override fun getItemCount() = data.size + headers.size + if (loadMoreEnabled) 1 else 0

    fun getHeadersCount(): Int = headers.size

    fun attachRecyclerView(recycler: RecyclerView) {
        this.mRecyclerView = recycler
    }

    fun notifyScrollStatus(recycler: RecyclerView) {
        this.mRecyclerView = recycler

        recycler.findParent(CoordinatorLayout::class.java)?.let { coordinator ->
            (coordinator as ViewGroup).findChild(AppBarLayout::class.java)?.let { appBar ->

                var lastOffset = -1

                (appBar as AppBarLayout).addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, offset ->
                    if(lastOffset == -1 || Math.abs(offset - lastOffset) > 100) {
                        lastOffset = offset
                        notifyScrollStateToCurrentViews(recycler, AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    }
                })
            }
        }


        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var lastOffset = -1
            var totalScrolled = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                (recycler.layoutManager as? LinearLayoutManager)?.let {
                    val offset = if(it.orientation == LinearLayoutManager.VERTICAL) {
                        dy
                    } else { dx }

                    totalScrolled += offset

                    if(lastOffset == -1 || Math.abs(totalScrolled - lastOffset) > 100) {
                        lastOffset = totalScrolled
                        notifyScrollStateToCurrentViews(recycler, AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                notifyScrollStateToCurrentViews(recycler, newState)
            }
        })
    }

    private fun notifyScrollStateToCurrentViews(recycler: RecyclerView, state: Int) {

        val recyclerRect = Rect()
        recycler.getGlobalVisibleRect(recyclerRect)
        val recyclerRectF = RectF(recyclerRect)

        recycler.forEachVisibleView { position ->

            recycler.findViewHolderForAdapterPosition(position)?.let {
                (it as? ViewHolder<*>)?.apply {

                    val rowRect = Rect()
                    it.containerView?.getGlobalVisibleRect(rowRect)
                    val rowRectF = RectF(rowRect)


                    this@PresenterAdapter.scrollState = state

                    setScrollState(state)

                    if (isScrollStopped(state)) {
                        onScrollStopped()
                    }

                    //Check effective visibility
                    if(recyclerRectF.contains(rowRectF) && !visible) {
                        onShowed()
                        visible = true
                    }
                }
            }
        }
    }

    private fun isScrollStopped(state: Int) = state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE

    /**
     * Add data at the end of the current data list and notifies the change
     * @param data items collection to append at the end of the current collection
     * @return PresenterAdapter called instance
     */
    fun addData(data: List<T>) {

        this.loadMoreInvoked = false
        val currentItemCount = itemCount
        this.data.addAll(data)
        val dataSize = data.size

        Handler().post {
            if (loadMoreEnabled) {
                notifyItemChanged(currentItemCount - 1)
            } else {
                notifyItemRangeInserted(currentItemCount, dataSize)
            }
        }
    }

    fun clearData() {
        this.data.clear()
        notifyDataSetChanged()
    }


    /**
     * Remove item object from data collection
     */
    fun removeItem(item: T) {
        if (this.data.contains(item)) {
            val pos = this.data.indexOf(item)
            this.data.remove(item)
            notifyItemRemoved(getPositionWithHeaders(pos))
        }
    }

    /**
     * Remove item position from data collection. Position argument does no take into account headers.
     */
    fun removeItem(position: Int) {
        if (position >= 0 && position < this.data.size) {
            this.data.removeAt(position)
            notifyItemRemoved(getPositionWithHeaders(position))
        }
    }

    /**
     * Swap two items. First item has position 0. If position corresponds with header position, exception is thrown.
     * @param from: absolute position in collection, taking into account headers
     * @param to:  absolute position in collection, taking into account headers
     *
     * @return Unit, or IllegalArgumentException if either from or to arguments are header positions.
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
            throw IndexOutOfBoundsException("Cannot swap items, data size is " + data.size)
        }

        if (from == to) {
            return
        }

        Collections.swap(data, from, to)

        notifyItemMoved(from, to)

    }

    /**
     * Move one item to another position, updating intermediates positions
     * @param from: absolute position in collection, taking into account headers
     * @param to:  absolute position in collection, taking into account headers
     *
     * @return Unit, or IllegalArgumentException if either from or to arguments are header positions.
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
            throw IndexOutOfBoundsException("Cannot move item, data size is " + data.size)
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
    fun enableLoadMore(loadMoreListener: (() -> Unit)?) {
        this.loadMoreEnabled = true
        this.loadMoreInvoked = false
        this.loadMoreListener = loadMoreListener
        notifyItemInserted(itemCount)
    }

    /**
     * Disable load more option
     */
    fun disableLoadMore() {
        if (this.loadMoreEnabled) {
            this.loadMoreEnabled = false
            this.loadMoreInvoked = false
            notifyItemRemoved(itemCount)
        }
    }

    fun isLoadMoreEnabled() = loadMoreEnabled

    override fun getItemId(position: Int): Long {
        if (isLoadMorePosition(position)) {
            return Companion.LOAD_MORE_TYPE.toLong()
        } else if (hasStableIds()) {
            return if (position < getHeadersCount()) headers[position].hashCode().toLong() else getItem(position).hashCode().toLong()
        } else {
            return super.getItemId(position)
        }
    }

    fun getData() = data


    fun enableAnimations(recyclerView: RecyclerView) {
        this.mRecyclerView = recyclerView
        this.enableAnimations = true
    }


}

class DiffUtilCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(p0: T, p1: T): Boolean {
        return if (p0 is Identifable<*> && p1 is Identifable<*>) {
            (p0 as Identifable<*>).getId() == (p1 as Identifable<*>).getId()
        } else {
            false
        }
    }

    override fun areContentsTheSame(p0: T, p1: T): Boolean {
        return p0 == p1
    }
}