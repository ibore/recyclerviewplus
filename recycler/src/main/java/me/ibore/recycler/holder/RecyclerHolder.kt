package me.ibore.recycler.holder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import me.ibore.recycler.adapter.RecyclerAdapter

@Suppress("UNCHECKED_CAST")
open class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {

        fun create(parentView: ViewGroup, @LayoutRes layoutId: Int): RecyclerHolder {
            return create(
                LayoutInflater.from(parentView.context).inflate(layoutId, parentView, false)
            )
        }

        fun create(itemView: View): RecyclerHolder {
            return RecyclerHolder(itemView)
        }

    }

    fun setMargin(left: Int, top: Int, right: Int, bottom: Int): RecyclerHolder {
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.setMargins(left, top, right, bottom)
        return this
    }

    fun setMargin(size: Int): RecyclerHolder {
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.setMargins(size, size, size, size)
        return this
    }

    fun setPadding(left: Int, top: Int, right: Int, bottom: Int): RecyclerHolder {
        itemView.setPadding(left, top, right, bottom)
        return this
    }

    fun setPadding(size: Int): RecyclerHolder {
        itemView.setPadding(size, size, size, size)
        return this
    }

    fun setBackground(background: Drawable): RecyclerHolder {
        itemView.background = background
        return this
    }

    fun setBackgroundColor(@ColorInt color: Int): RecyclerHolder {
        itemView.setBackgroundColor(color)
        return this
    }

    fun setBackgroundResource(@DrawableRes id: Int): RecyclerHolder {
        itemView.setBackgroundResource(id)
        return this
    }

    fun addOnItemClickListener(adapter: RecyclerAdapter<Any>, position: Int): RecyclerHolder {
        itemView.setOnClickListener {
            adapter.onItemClickListener?.onItemClick(this, adapter.getData(position), position)
        }
        return this
    }

    fun addOnItemLongClickListener(adapter: RecyclerAdapter<Any>, position: Int): RecyclerHolder {
        itemView.setOnLongClickListener {
            if (null != adapter.onItemLongClickListener) {
                adapter.onItemLongClickListener!!.onItemLongClick(
                    this,
                    adapter.getData(position),
                    position
                )
            } else {
                false
            }
        }
        return this
    }

    fun addOnItemChildClickListener(
        adapter: RecyclerAdapter<Any>,
        @IdRes idRes: Int,
        position: Int
    ): RecyclerHolder {
//        viewHolder.onClickListener(idRes, View.OnClickListener {
//            adapter.onItemChildClickListener?.onItemClick(this, idRes, adapter.getData(position), position)
//        })
        return this
    }

    fun addOnItemChildLongClickListener(
        adapter: RecyclerAdapter<Any>,
        @IdRes idRes: Int,
        position: Int
    ): RecyclerHolder {
//        viewHolder.onLongClickListener(idRes, View.OnLongClickListener {
//            if (null != adapter.onItemChildLongClickListener) {
//                return@OnLongClickListener adapter.onItemChildLongClickListener!!.onItemLongClick(this, idRes, adapter.getData(position), position)
//            } else {
//                return@OnLongClickListener false
//            }
//        })
        return this
    }
}