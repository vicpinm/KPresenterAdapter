package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.SimplePresenterAdapter
import com.vicpin.kpresenteradapter.SingleLinePresenterAdapter
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.sample.R
import com.vicpin.sample.di.Injector
import com.vicpin.sample.extensions.finishIdlingResource
import com.vicpin.sample.extensions.showToast
import com.vicpin.sample.extensions.startIdlingResource
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.view.adapter.CountryView
import com.vicpin.sample.view.adapter.HeaderView
import com.vicpin.sample.view.interfaces.ItemDeletedListener
import com.vicpin.sample.view.interfaces.ItemRecycledListener
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Victor on 25/06/2016.
 */
class ManualBindingFragment : Fragment(), ItemRecycledListener, ItemDeletedListener<Country> {

    private var lastPresentersRecycled: Int = 0
    private var currentPage: Int = 0
    private lateinit var adapter: PresenterAdapter<Country>
    private var isSingleAdapter = false
    private lateinit var repository: IRepository<Country>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.repository = Injector.get().getCountryRepository()
        initView()
    }

    private fun initView(){
        setupAdapter()
        appendListeners()
        setupRecyclerView()
        loadFirstData()
    }

    private fun setupAdapter() {
        if(isSingleAdapter){
            adapter = SingleLinePresenterAdapter(R.layout.adapter_country_single_line)
        }
        else {
            adapter = SimplePresenterAdapter(CountryView::class, R.layout.adapter_country)
        }
        adapter.notifyScrollStopped(recycler)
        adapter.enableLoadMore { onLoadMore() }

    }

    private fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast(getString(R.string.toast_message,item.name)) }
            itemLongClickListener = { item, view -> showToast("Country long pressed: " + item.name) }
            customListener = this@ManualBindingFragment
        }
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recycler.adapter = adapter
    }

    private fun loadFirstData() {
        val data = repository.getItemsPage(0)
        adapter.setData(data)
        adapter.addHeader(R.layout.adapter_header, HeaderView::class)
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

