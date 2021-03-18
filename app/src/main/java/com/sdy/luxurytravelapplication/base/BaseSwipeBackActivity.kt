package com.cxz.wanandroid.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.cxz.swipelibrary.SwipeBackActivityBase
import com.cxz.swipelibrary.SwipeBackActivityHelper
import com.cxz.swipelibrary.SwipeBackLayout
import com.cxz.swipelibrary.Utils
import com.sdy.luxurytravelapplication.base.BaseActivity

/**
 * Created by chenxz on 2018/8/6.
 */
abstract class BaseSwipeBackActivity<VB : ViewBinding> : BaseActivity<VB>(), SwipeBackActivityBase {

    private lateinit var mHelper: SwipeBackActivityHelper

    /**
     * SwipeBack Enable
     */
    open fun enableSwipeBack(): Boolean = true

    /**
     * 初始化 SwipeBack
     */
    private fun initSwipeBack() {
        setSwipeBackEnable(enableSwipeBack())
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper.onActivityCreate()
        initSwipeBack()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper.onPostCreate()
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper.getSwipeBackLayout()
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }

}