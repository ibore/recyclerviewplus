package me.ibore.recycler.listener

import androidx.annotation.IdRes
import me.ibore.recycler.holder.RecyclerHolder

interface OnItemChildClickListener<D> {

    fun onItemClick(holder: RecyclerHolder, @IdRes idRes: Int, data: D?, position: Int)

}