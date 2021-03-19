package com.sdy.luxurytravelapplication.ext

import com.blankj.utilcode.util.NetworkUtils
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.http.exception.ErrorStatus
import com.sdy.luxurytravelapplication.http.exception.ExceptionHandle
import com.sdy.luxurytravelapplication.http.function.RetryWithDelay
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseBean
import com.cxz.wanandroid.rx.SchedulerUtils
import com.sdy.luxurytravelapplication.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @author chenxz
 * @date 2018/11/22
 * @desc
 */

fun <T : BaseBean> Observable<T>.ss(
    model: IModel?,
    view: IView?,
    isShowLoading: Boolean = true,
    onSuccess: (T) -> Unit
) {
    this.compose(SchedulerUtils.ioToMain())
            .retryWhen(RetryWithDelay())
            .subscribe(object : Observer<T> {
                override fun onComplete() {
                    view?.hideLoading()
                }

                override fun onSubscribe(d: Disposable) {
                    if (isShowLoading) view?.showLoading()
                    model?.addDisposable(d)
                    if (!NetworkUtils.isConnected()) {
                        view?.showDefaultMsg(TravelApp.instance.resources.getString(R.string.network_unavailable_tip))
                        onComplete()
                    }
                }

                override fun onNext(t: T) {
                    when {
                        t.code == ErrorStatus.SUCCESS -> onSuccess.invoke(t)
                        t.code == ErrorStatus.TOKEN_INVALID -> {
                            // Token 过期，重新登录
                        }
                        else -> view?.showDefaultMsg(t.msg)
                    }
                }

                override fun onError(t: Throwable) {
                    view?.hideLoading()
                    view?.showError(ExceptionHandle.handleException(t))
                }
            })
}

fun <T : BaseBean> Observable<T>.sss(
    view: IView?,
    isShowLoading: Boolean = true,
    onSuccess: (T) -> Unit,
    onError: ((T) -> Unit)? = null
): Disposable {
    if (isShowLoading) view?.showLoading()
    return this.compose(SchedulerUtils.ioToMain())
            .retryWhen(RetryWithDelay())
            .subscribe({
                when {
                    it.code == ErrorStatus.SUCCESS -> onSuccess.invoke(it)
                    it.code == ErrorStatus.TOKEN_INVALID -> {
                        // Token 过期，重新登录
                    }
                    else -> {
                        if (onError != null) {
                            onError.invoke(it)
                        } else {
                            if (it.msg.isNotEmpty())
                                view?.showDefaultMsg(it.msg)
                        }
                    }
                }
                view?.hideLoading()
            }, {
                view?.hideLoading()
                view?.showError(ExceptionHandle.handleException(it))
            })
}


fun <T : BaseBean> Observable<T>.ssss(
    view: IView?=null,
    isShowLoading: Boolean = true,
    onResult: (T) -> Unit
): Disposable {
    if (isShowLoading) view?.showLoading()
    return this.compose(SchedulerUtils.ioToMain())
            .retryWhen(RetryWithDelay())
            .subscribe({
                when (it.code) {
                    ErrorStatus.TOKEN_INVALID -> {
                        // Token 过期，重新登录
                    }
                    else -> {
                        onResult.invoke(it)
                    }
                }
                view?.hideLoading()
            }, {
                view?.hideLoading()
                view?.showError(ExceptionHandle.handleException(it))
            })
}

