package me.ibore.recycler.adapter

import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.ibore.recycler.animation.BaseAnimation
import me.ibore.recycler.holder.ItemHolder
import me.ibore.recycler.holder.LoadHolder
import me.ibore.recycler.holder.LoadMoreHolder
import me.ibore.recycler.holder.RecyclerHolder
import me.ibore.recycler.listener.*

@Suppress("UNREACHABLE_CODE")
abstract class RecyclerAdapter<D> : RecyclerView.Adapter<RecyclerHolder>(), IRecyclerAdapter<D> {

    companion object {
        // 头布局
        private const val HEADER = 9000 - 1

        // 脚布局
        private const val FOOTER = HEADER - 1

        // 状态布局
        private const val STATUS = FOOTER - 1

        // 加载更多布局
        private const val MORE = STATUS - 1

        const val STATUS_LOAD: Int = 1
        const val STATUS_EMPTY: Int = 2
        const val STATUS_ERROR: Int = 3
    }

    // 点击事件监听
    var onItemClickListener: OnItemClickListener<D>? = null
    var onItemLongClickListener: OnItemLongClickListener<D>? = null
    var onItemChildClickListener: OnItemChildClickListener<D>? = null
    var onItemChildLongClickListener: OnItemChildLongClickListener<D>? = null
    private var onLoadListener: OnLoadListener? = null
    private var onLoadMoreListener: OnLoadMoreListener? = null

    private var datas: MutableList<D>
    private var isShowItem: Boolean = true
    private var headerHolder: ItemHolder? = null
    private var loadHolder: LoadHolder? = null
    private var footerHolder: ItemHolder? = null
    private var loadMoreHolder: LoadMoreHolder? = null

    private var mIsShowContent = false
    private var mCanLoadingMore = false

    private var mAnimator: BaseAnimation? = null
    private var mLastPosition = -1
    private var isAnimatorFirstOnly = true
    private var mAnimatorDuration: Long? = null
    private var mAnimatorInterpolator: Interpolator? = null

    init {
        this.datas = ArrayList()
        /*registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

            }
        })*/
    }

    override fun setDatas(datas: MutableList<D>) {
        if (datas.isNullOrEmpty()) {
            this.datas.clear()
        } else {
            this.datas = datas
            notifyDataSetChanged()
        }
    }

    override fun addDatas(datas: MutableList<D>) {
        if (datas.isNullOrEmpty()) return
        this.datas.addAll(datas)
    }

    override fun addData(data: D) {
        datas.add(data)
        notifyItemInserted(if (hasHeaderHolder()) datas.size else datas.size - 1)
    }

    override fun addData(data: D, dataPosition: Int) {
        datas.add(dataPosition, data)
        val adapterPosition = if (hasHeaderHolder()) dataPosition + 1 else dataPosition
        notifyItemInserted(adapterPosition)
    }

    override fun getData(dataPosition: Int): D {
        return datas[dataPosition]
    }

    override fun getDatas(): MutableList<D> {
        return datas
    }

    override fun removeData(dataPosition: Int) {
        datas.removeAt(dataPosition)
        val position = if (hasHeaderHolder()) dataPosition + 1 else dataPosition
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    override fun removeData(data: D) {
        val index = datas.indexOf(data)
        if (index >= 0) {
            removeData(index)
        }
    }

    override fun clearData() {
        datas.clear()
        notifyDataSetChanged()
    }

    open fun setAnimatorFirstOnly(isFirstOnly: Boolean) {
        this.isAnimatorFirstOnly = isFirstOnly
    }

    open fun setAnimator(animator: BaseAnimation) {
        this.setAnimator(animator, 300)
    }

    open fun setAnimator(animator: BaseAnimation, duration: Long) {
        this.setAnimator(animator, duration, LinearInterpolator())
    }

    open fun setAnimator(animator: BaseAnimation, duration: Long, value: Interpolator) {
        this.mAnimator = animator
        this.mAnimatorDuration = duration
        this.mAnimatorInterpolator = value
    }

    open fun clearAnimator(v: View) {
        v.rotationX = 0f
        v.rotationY = 0f
        v.rotation = 0f
        v.scaleX = 1f
        v.scaleY = 1f
        v.alpha = 1f
        v.translationX = 0f
        v.translationY = 0f
        v.pivotX = v.measuredWidth / 2f
        v.pivotY = v.measuredHeight / 2f
        v.animate().setInterpolator(null).startDelay = 0
    }

    final override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        when (getItemViewType(position)) {
            STATUS -> loadHolder!!.onBindRecyclerHolder(holder)
            HEADER -> headerHolder!!.onBindRecyclerHolder(holder)
            FOOTER -> footerHolder!!.onBindRecyclerHolder(holder)
            MORE -> loadMoreHolder!!.onBindRecyclerHolder(holder)
            else -> {
                val dataPosition = getDataPosition(position)
                onBindRecyclerHolder(holder, getData(dataPosition), dataPosition, getItemViewType(position))
                if (null != mAnimator && null != mAnimatorDuration && null != mAnimatorInterpolator) {
                    if (!isAnimatorFirstOnly || dataPosition > mLastPosition) {
                        for (anim in mAnimator!!.getAnimators(holder.itemView)) {
                            anim.duration = mAnimatorDuration!!
                            anim.interpolator = mAnimatorInterpolator!!
                            anim.start()
                        }
                        mLastPosition = dataPosition
                    } else {
                        clearAnimator(holder.itemView)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return when (viewType) {
            STATUS -> loadHolder!!.onCreateRecyclerHolder(parent)
            HEADER -> headerHolder!!.onCreateRecyclerHolder(parent)
            FOOTER -> footerHolder!!.onCreateRecyclerHolder(parent)
            MORE -> loadMoreHolder!!.onCreateRecyclerHolder(parent)
            else -> onCreateRecyclerHolder(parent, viewType)
        }
    }

    final override fun getItemCount(): Int {
        if (isShowStatusView()) return 1
        var itemCount = getRecyclerItemCount()
        if (hasHeaderHolder()) itemCount++
        if (hasFooterHolder()) itemCount++
        if (null != loadMoreHolder) itemCount++
        return itemCount
    }

    final override fun getItemViewType(position: Int): Int {
        return when {
            isShowStatusView() -> STATUS
            isHeaderHolder(position) -> HEADER
            isFooterHolder(position) -> FOOTER
            isMoreHolder(position) -> MORE
            else -> getRecyclerItemType(datas[getDataPosition(position)], getDataPosition(position))
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerHolder) {
        super.onViewAttachedToWindow(holder)
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            when {
                isShowStatusView() -> layoutParams.isFullSpan = true
                isHeaderHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                isFooterHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                isMoreHolder(holder.layoutPosition) -> layoutParams.isFullSpan = true
                else -> layoutParams.isFullSpan = isStaggeredFullSpan(getData(getDataPosition(holder.layoutPosition)),getDataPosition(holder.layoutPosition))
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isShowStatusView() || isHeaderHolder(position) || isMoreHolder(position)) {
                        layoutManager.spanCount
                    } else {
                        getGridSpanSize(layoutManager.spanCount, getData(getDataPosition(position)), getDataPosition(position))
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onLoadingMore(layoutManager)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onLoadingMore(layoutManager)
            }
        })
    }

    protected open fun onLoadingMore(layoutManager: RecyclerView.LayoutManager?) {
        if (null != onLoadMoreListener && null != loadMoreHolder && loadMoreHolder!!.status == STATUS_LOAD
                && null != layoutManager && mCanLoadingMore) {
            val lastVisibleItem = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is StaggeredGridLayoutManager -> {
                    val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                    var max = lastVisibleItemPositions[0]
                    for (value in lastVisibleItemPositions) {
                        if (value > max) max = value
                    }
                    max
                }
                else -> -1
            }
            val totalItemCount = layoutManager.itemCount
            if (lastVisibleItem == totalItemCount - 1) {
                mCanLoadingMore = false
                onLoadMoreListener?.onLoadMoreLoading()
            }
        }
    }

    protected fun isStaggeredFullSpan(data: D, dataPosition: Int): Boolean = false

    protected open fun getGridSpanSize(spanCount: Int, data: D, dataPosition: Int): Int = 1

    override fun onViewDetachedFromWindow(holder: RecyclerHolder) {
        super.onViewDetachedFromWindow(holder)
        //holder.viewHolder.destroy()
    }

    open fun hasHeaderHolder(): Boolean {
        return null != headerHolder
    }

    open fun hasFooterHolder(): Boolean {
        return null != footerHolder
    }

    open fun isShowStatusView(): Boolean {
        return !isShowItem && null != loadHolder
    }

    private fun isHeaderHolder(position: Int): Boolean = position == 0 && hasHeaderHolder()

    private fun isFooterHolder(position: Int): Boolean {
        if (null == footerHolder) return false
        var po = position
        if (hasHeaderHolder()) po -= 1
        return po == datas.size
    }

    private fun isMoreHolder(position: Int): Boolean {
        if (null == loadMoreHolder) return false
        var po = position
        if (hasHeaderHolder()) po -= 1
        if (hasFooterHolder()) po -= 1
        return po == datas.size
    }

    private fun getMoreHolderPosition(): Int {
        return if (isShowItem && null != loadMoreHolder) {
            var position = datas.size
            if (hasHeaderHolder()) position++
            if (hasFooterHolder()) position++
            position
        } else -1
    }

    private fun getDataPosition(position: Int): Int {
        return if (hasHeaderHolder()) position - 1 else position
    }

    override fun getRecyclerItemType(data: D, dataPosition: Int): Int = 0

    override fun getRecyclerItemCount(): Int = datas.size

    open fun setLoadHolder(loadingId: Int, emptyId: Int, errorId: Int) {
        setLoadHolder(LoadHolder.create(loadingId, emptyId, errorId))
    }

    open fun setLoadHolder(loadingView: View, emptyView: View, errorView: View) {
        setLoadHolder(LoadHolder.create(loadingView, emptyView, errorView))
    }

    open fun setLoadHolder(loadHolder: LoadHolder) {
        this.loadHolder = loadHolder
        if (!isShowItem) notifyDataSetChanged()
    }

    open fun removeStatusHolder() {
        if (null != loadHolder) {
            loadHolder = null
            notifyDataSetChanged()
        }
    }

    open fun showItemView() {
        isShowItem = true
        notifyDataSetChanged()
    }

    open fun showLoadingView() {
        if (null != loadHolder) {
            isShowItem = false
            loadHolder!!.status = STATUS_LOAD
            mCanLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun showEmptyView() {
        if (null != loadHolder) {
            isShowItem = false
            loadHolder!!.status = STATUS_EMPTY
            loadHolder!!.onLoadListener = onLoadListener
            mCanLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun showErrorView() {
        if (null != loadHolder) {
            isShowItem = false
            loadHolder!!.status = STATUS_ERROR
            loadHolder!!.onLoadListener = onLoadListener
            mCanLoadingMore = false
            notifyDataSetChanged()
        }
    }

    open fun setLoadingMoreHolder(loadingId: Int, emptyId: Int, errorId: Int) {
        setLoadingMoreHolder(LoadMoreHolder.create(loadingId, emptyId, errorId))
    }

    open fun setLoadingMoreHolder(loadingView: View, emptyView: View, errorView: View) {
        setLoadingMoreHolder(LoadMoreHolder.create(loadingView, emptyView, errorView))
    }

    open fun setLoadingMoreHolder(loadMoreHolder: LoadMoreHolder) {
        this.loadMoreHolder = loadMoreHolder
        if (isShowItem) notifyDataSetChanged()
    }

    open fun removeMoreHolder() {
        if (null != loadMoreHolder) {
            loadMoreHolder = null
            notifyDataSetChanged()
        }
    }

    open fun showLoadingMoreView() {
        if (null != loadMoreHolder && isShowItem) {
            loadMoreHolder!!.status = STATUS_LOAD
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun showEmptyMoreView() {
        if (null != loadMoreHolder && isShowItem) {
            loadMoreHolder!!.status = STATUS_EMPTY
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun showErrorMoreView() {
        if (null != loadMoreHolder && isShowItem) {
            loadMoreHolder!!.status = STATUS_ERROR
            loadMoreHolder!!.onLoadMoreListener = onLoadMoreListener
            notifyItemChanged(getMoreHolderPosition())
        }
    }

    open fun setHeaderHolder(headerHolder: ItemHolder?) {
        this.headerHolder = headerHolder
        notifyDataSetChanged()
    }

    open fun setFooterHolder(footerHolder: ItemHolder?) {
        this.footerHolder = footerHolder
        notifyDataSetChanged()
    }
}