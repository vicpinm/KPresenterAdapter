package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.sample.R
import com.vicpin.sample.di.Injector
import com.vicpin.sample.extensions.finishIdlingResource
import com.vicpin.sample.extensions.showToast
import com.vicpin.sample.extensions.startIdlingResource
import com.vicpin.sample.model.*
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * Created by Victor on 25/06/2016.
 */
class AutoBindingFragment : Fragment() {
    
    private var currentPage: Int = 0
    private lateinit var adapter: PresenterAdapter<Town>
    private lateinit var repository: IRepository<Town>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.repository = Injector.get().getTownRepository()
        initView()
    }

    private fun initView(){
        setupAdapter()
        appendListeners()
        setupRecyclerView()
    }

    private fun setupAdapter() {
        val data = repository.getItemsPage(0)
        adapter = TownPresenterAdapter(R.layout.adapter_town)
        adapter.setData(data)
        adapter.enableLoadMore { onLoadMore() }


    }

    private fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast("Country clicked: " + item.name) }
            itemLongClickListener = { item, view -> showToast("Country long pressed: " + item.name) }
            customListener = this@AutoBindingFragment
        }
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
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


}
