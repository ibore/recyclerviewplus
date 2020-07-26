package me.ibore.recycler.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import me.ibore.recycler.adapter.RecyclerAdapter
import me.ibore.recycler.listener.OnLoadMoreListener
import me.ibore.utils.SizeUtils

class LoadMoreHolder : ItemHolder {

    companion object {
        fun create(loadingId: Int, emptyId: Int, errorId: Int): LoadMoreHolder {
            return LoadMoreHolder(loadingId, emptyId, errorId)
        }

        fun create(loadingView: View, emptyView: View, errorView: View): LoadMoreHolder {
            return LoadMoreHolder(loadingView, emptyView, errorView)
        }
    }

    var loadingId: Int = 0
    var emptyId: Int = 0
    var errorId: Int = 0

    var loadingView: View? = null
    var emptyView: View? = null
    var errorView: View? = null

    var status: Int = RecyclerAdapter.STATUS_LOAD
    var onLoadMoreListener: OnLoadMoreListener? = null

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
                ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(48F))
        val inflater = LayoutInflater.from(parent.context)
        frameLayout.addView(loadingView ?: inflater.inflate(loadingId, parent, false))
        frameLayout.addView(emptyView ?: inflater.inflate(emptyId, parent, false))
        frameLayout.addView(errorView ?: inflater.inflate(errorId, parent, false))
        return RecyclerHolder.create(frameLayout)
    }

    override fun onBindRecyclerHolder(holder: RecyclerHolder) {
        val frameLayout = holder.itemView as FrameLayout
        when (status) {
            RecyclerAdapter.STATUS_LOAD -> {
                frameLayout.getChildAt(0).visibility = View.VISIBLE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            RecyclerAdapter.STATUS_EMPTY -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.VISIBLE
                frameLayout.getChildAt(2).visibility = View.GONE
            }
            RecyclerAdapter.STATUS_ERROR -> {
                frameLayout.getChildAt(0).visibility = View.GONE
                frameLayout.getChildAt(1).visibility = View.GONE
                frameLayout.getChildAt(2).visibility = View.VISIBLE
                frameLayout.getChildAt(2).setOnClickListener { v: View? ->
                    if (null != onLoadMoreListener) {
                        onLoadMoreListener!!.onLoadMoreError()
                    }
                }
            }
        }
    }

}