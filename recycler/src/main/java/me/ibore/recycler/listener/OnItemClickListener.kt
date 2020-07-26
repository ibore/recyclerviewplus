package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder

interface OnItemClickListener<D> {

    fun onItemClick(holder: RecyclerHolder, data: D?, position: Int)

}