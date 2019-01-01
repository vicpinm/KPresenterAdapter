package com.vicpin.sample.model

import java.util.ArrayList


interface IRepository<T> {

    val PAGE_SIZE: Int
    var items: List<T>

    fun getAllItems(): List<T> = items

    fun getItemsPage(page: Int): List<T> {
        val startIndex = page * PAGE_SIZE
        var endIndex = page * PAGE_SIZE + PAGE_SIZE
        val countries = getAllItems()

        if (startIndex >= countries.size) {
            return ArrayList()
        }

        if (endIndex > countries.size) {
            endIndex = countries.size
        }

        return countries.subList(startIndex, endIndex)
    }
}