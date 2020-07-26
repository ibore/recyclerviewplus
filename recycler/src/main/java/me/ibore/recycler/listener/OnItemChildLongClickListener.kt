package me.ibore.recycler.listener

import androidx.annotation.IdRes
import me.ibore.recycler.holder.RecyclerHolder

interface OnItemChildLongClickListener<D> {

    fun onItemLongClick(holder: RecyclerHolder, @IdRes idRes: Int, data: D?, position: Int): Boolean

}