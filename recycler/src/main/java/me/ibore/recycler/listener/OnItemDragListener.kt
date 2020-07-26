package me.ibore.recycler.listener

import me.ibore.recycler.holder.RecyclerHolder


interface OnItemDragListener {

    fun onItemDragStart(holder: RecyclerHolder, position: Int)

    fun onItemDragMoving(source: RecyclerHolder, fromPosition: Int, target: RecyclerHolder, toPosition: Int)

    fun onItemDragEnd(holder: RecyclerHolder, position: Int)

}