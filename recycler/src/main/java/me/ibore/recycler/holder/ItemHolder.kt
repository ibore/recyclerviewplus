package me.ibore.recycler.holder

import android.view.ViewGroup

interface ItemHolder {

    fun onCreateRecyclerHolder(parent: ViewGroup): RecyclerHolder

    fun onBindRecyclerHolder(holder: RecyclerHolder)

}