package com.sdy.luxurytravelapplication.rx

import com.blankj.utilcode.util.NetworkUtils
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.http.exception.ErrorStatus
import com.sdy.luxurytravelapplication.http.exception.ExceptionHandle
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseBean
import io.reactivex.subscribers.ResourceSubscriber

/**
 * Created by chenxz on 2018/6/11.
 */
abstract class BaseSubscriber<T : BaseBean> : ResourceSubscriber<T> {

    private var mView: IView? = null
    private var mErrorMsg = ""
    private var bShowLoading = true

    constructor(view: IView) {
        this.mView = view
    }

    constructor(view: IView, bShowLoading: Boolean) {
        this.mView = view
        this.bShowLoading = bShowLoading
    }

    /**
     * 成功的回调
     */
    protected abstract fun onSuccess(t: T)

    /**
     * 错误的回调
     */
    protected fun onError(t: T) {}

    override fun onStart() {
        super.onStart()
        if (bShowLoading) mView?.showLoading()
        if (!NetworkUtils.isConnected()) {
            mView?.showDefaultMsg("当前网络不可用，请检查网络设置")
            onComplete()
        }
    }

    override fun onNext(t: T) {
        mView?.hideLoading()
        when {
            t.code == ErrorStatus.SUCCESS -> onSuccess(t)
            t.code == ErrorStatus.TOKEN_INVALID -> {
                // TODO Token 过期，重新登录
            }
            else -> {
                onError(t)
                if (t.msg.isNotEmpty())
                    mView?.showDefaultMsg(t.msg)
            }
        }
    }

    override fun onError(e: Throwable) {
        mView?.hideLoading()
        if (mView == null) {
            throw RuntimeException("mView can not be null")
        }
        if (mErrorMsg.isEmpty()) {
            mErrorMsg = ExceptionHandle.handleException(e)
        }
        mView?.showDefaultMsg(mErrorMsg)
    }

    override fun onComplete() {
        mView?.hideLoading()
    }

}