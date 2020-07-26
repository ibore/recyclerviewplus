package me.ibore.recycler.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_EMPTY
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_ERROR
import me.ibore.recycler.adapter.RecyclerAdapter.Companion.STATUS_LOAD
import me.ibore.recycler.listener.OnLoadListener

class LoadHolder : ItemHolder {

    companion object {
        fun create(loadingId: Int, emptyId: Int, errorId: Int): LoadHolder {
            return LoadHolder(loadingId, emptyId, errorId)
        }

        fun create(loadingView: View, emptyView: View, errorView: View): LoadHolder {
            return LoadHolder(loadingView, emptyView, errorView)
        }
    }

    var loadingId: Int = 0
    var emptyId: Int = 0
    var errorId: Int = 0

    var loadingView: View? = null
    var emptyView: View? = null
    var errorView: View? = null

    var status: Int = STATUS_LOAD
    var onLoadListener: OnLoadListener? = null

    constructor(loadingId: Int, emptyId: Int, errorId: Int) {
        this.loadingId = loadingId
        this.emptyId = emptyId
        this.errorId = errorId
    }

    constructor(loadingView: View, emptyView: View, errorView: View) {
        this.loadingView = loadingView
        this.emptyView = emptyView
        this.errorView = errorView
    }

    override fun onCreateRecyclerHolder(parent: ViewGroup): RecyclerHolder {
        val frameLayout = FrameLayout(parent.context)
        frameLayout.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(parent.context)
        frameLayout.addView(loadingView ?: inflater.inflate(loadingId, parent, false))
        frameLayout.addView(emptyView ?: inflater.inflate(emptyId, parent, false))
        frameLayout.addView(errorView ?: inflater.inflate(errorId, parent, false))
        return RecyclerHolder.create(frameLayout)
    }

    override fun onBindRecyclerHolder(holder: RecyclerHolder) {
        val frameLayout = holder.itemView as FrameLayout
        when (status) {
            STATUS_LOAD -> {
                frameLayout.getChildAt(0).visibility = View.VISIBLE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            STATUS_EMPTY -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.VISIBLE
                frameLayout.getChildAt(2).visibility = View.GONE
                frameLayout.getChildAt(1).setOnClickListener { onLoadListener?.onLoadEmpty() }
            }
            STATUS_ERROR -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.VISIBLE
                frameLayout.getChildAt(2).setOnClickListener { onLoadListener?.onLoadError() }
            }
        }
    }

}