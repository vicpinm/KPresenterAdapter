package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.sample.R
import com.vicpin.sample.data.MixedRepository
import com.vicpin.sample.extensions.finishIdlingResource
import com.vicpin.sample.extensions.showToast
import com.vicpin.sample.extensions.startIdlingResource
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.model.NamedItem
import com.vicpin.sample.view.adapter.MultiBindingAdapter
import com.vicpin.sample.view.interfaces.ItemDeletedListener
import com.vicpin.sample.view.interfaces.ItemRecycledListener
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Victor on 25/06/2016.
 */
class MultiBindingFragment : Fragment(), ItemRecycledListener, ItemDeletedListener<Country> {

    private var lastPresentersRecycled: Int = 0
    private var currentPage: Int = 0
    private lateinit var adapter: MultiBindingAdapter
    private var isSingleAdapter = false
    private lateinit var repository: IRepository<NamedItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.repository = MixedRepository(context)
        initView()
    }

    private fun initView(){
        setupAdapter()
        appendListeners()
        setupRecyclerView()
        loadFirstData()
    }

    private fun setupAdapter() {
        adapter = MultiBindingAdapter()
        adapter.notifyScrollStatus(recycler)
        adapter.enableLoadMore { onLoadMore() }

    }

    private fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast(getString(R.string.item_toast_message,item.name)) }
            customListener = this@MultiBindingFragment
        }
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
    }

    private fun loadFirstData() {
        val data = repository.getItemsPage(0)
        adapter.setData(data)
    }

    override fun onItemRecycled(presenterId: Int) {
        lastPresenterDestroyed.text = "Last presenters recycled: $lastPresentersRecycled - $presenterId"
        lastPresentersRecycled = presenterId
    }

    /**
     * Pagination listener. Simulates a 1500ms load delay.
     */
    private fun onLoadMore() {
        startIdlingResource()
        Handler().postDelayed({
            currentPage++
            val newData = repository.getItemsPage(currentPage)
            if (newData.isNotEmpty()) {
                adapter.addData(newData)
            } else {
                adapter.disableLoadMore()
            }
            finishIdlingResource()
        }, 1500)

    }

    override fun onItemDeleted(item: Country) {
        adapter.updateHeaders()
    }

    fun toggleAdapter() {
        isSingleAdapter = !isSingleAdapter
        initView()
    }
}

