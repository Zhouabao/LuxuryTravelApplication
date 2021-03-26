package com.sdy.luxurytravelapplication.widgets

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R

class GalleryOnScrollListener  : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

   override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        slideInAnimation(recyclerView)
    }

    /**
     * 滑入时的动画
     */
    open fun slideInAnimation(recyclerView: RecyclerView) {
        val percent: Float = getScrollPercent(recyclerView)
        if (percent > 0 && percent < 1) {
            //屏幕中只能同时显示两个item，所以直接找到第二个可见item就是需要实现动画效果的item
            val targetView: View = getTargetView(recyclerView, 1) ?: return
            //根据滑动的距离改变item的大小
            if (recyclerView.childCount == 1) {
                val lp = targetView.layoutParams as MarginLayoutParams
                lp.width = SizeUtils.dp2px(250f)
                lp.height = SizeUtils.dp2px(250f)
                targetView.layoutParams = lp
            } else {
                val lp = targetView.layoutParams as MarginLayoutParams
                lp.width = (SizeUtils.dp2px(280f) * (0.89 + 0.11 * percent)).toInt()
                lp.height = (SizeUtils.dp2px(280f) * (0.89 + 0.11 * percent)).toInt()
                targetView.layoutParams = lp
            }
        }
    }

    /**
     * 计算滑动的百分比
     */
    open fun getScrollPercent(recyclerView: RecyclerView): Float {
        val firstItem = recyclerView.getChildAt(0) ?: return 0F
        val layoutManager = recyclerView.layoutManager
        val scrollDistance = firstItem.width - layoutManager!!.getDecoratedRight(firstItem)
        return scrollDistance * 1.0f / firstItem.width
    }

    open fun getTargetView(recyclerView: RecyclerView, index: Int): View? {
        val view = recyclerView.getChildAt(index) ?: return null
        return view.findViewById(R.id.ivUser)
    }
}
