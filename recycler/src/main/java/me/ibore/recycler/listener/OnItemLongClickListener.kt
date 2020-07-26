package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder

interface OnItemLongClickListener<D> {

    fun onItemLongClick(holder: RecyclerHolder, data: D?, position: Int): Boolean

}