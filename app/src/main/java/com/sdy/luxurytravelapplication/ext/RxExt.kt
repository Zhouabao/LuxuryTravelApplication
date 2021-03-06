package com.sdy.luxurytravelapplication.ext

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.cxz.wanandroid.rx.SchedulerUtils
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.http.exception.ErrorStatus
import com.sdy.luxurytravelapplication.http.exception.ExceptionHandle
import com.sdy.luxurytravelapplication.http.function.RetryWithDelay
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseBean
import com.sdy.luxurytravelapplication.ui.dialog.LoadingDialog
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @author chenxz
 * @date 2018/11/22
 * @desc
 */

fun <T : BaseBean> Observable<T>.ss(
    model: IModel? = null,
    view: IView? = null,
    isShowLoading: Boolean = false,
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
                    view?.showNetError(TravelApp.instance.resources.getString(R.string.network_unavailable_tip))
                    onComplete()
                }
            }

            override fun onNext(t: T) {
                when {
                    t.code == ErrorStatus.SUCCESS -> onSuccess.invoke(t)
                    t.code == ErrorStatus.TOKEN_INVALID -> {
                        // Token 过期，重新登录
                        MessageDialog.show(
                            ActivityUtils.getTopActivity() as AppCompatActivity,
                            ActivityUtils.getTopActivity().getString(R.string.login_expired),
                            ActivityUtils.getTopActivity().getString(R.string.please_relogin)
                        )
                            .setCancelable(false)
                            .setOnOkButtonClickListener { baseDialog, v ->
                                CommonFunction.loginOut(ActivityUtils.getTopActivity())
                                false
                            }
                    }
                    else -> view?.showMsg(t.msg)
                }
            }

            override fun onError(t: Throwable) {
                view?.hideLoading()
                view?.showError(ExceptionHandle.handleException(t))
            }

        })
}

fun <T : BaseBean> Observable<T>.sss(
    view: IView? = null,
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
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        ActivityUtils.getTopActivity().getString(R.string.login_expired),
                        ActivityUtils.getTopActivity().getString(R.string.please_relogin)
                    )
                        .setCancelable(false)
                        .setOnOkButtonClickListener { baseDialog, v ->
                            CommonFunction.loginOut(ActivityUtils.getTopActivity())
                            false
                        }
                }
                else -> {
                    if (onError != null) {
                        onError.invoke(it)
                    } else {
                        if (it.msg.isNotEmpty())
                            view?.showMsg(it.msg)
                    }
                }
            }
            view?.hideLoading()
        }, {
            view?.hideLoading()
            view?.showNetError(ExceptionHandle.handleException(it))
        })
}


fun <T : BaseBean> Observable<T>.ssss(
    view: IView? = null,
    isShowLoading: Boolean = false,
    loadingDialog: LoadingDialog? = null,
    onResult: (T) -> Unit
): Disposable {
    if (isShowLoading) view?.showLoading()
    return this.compose(SchedulerUtils.ioToMain())
        .retryWhen(RetryWithDelay())
        .subscribe({
            when (it.code) {
                ErrorStatus.TOKEN_INVALID -> {
                    // Token 过期，重新登录
                    // Token 过期，重新登录
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        ActivityUtils.getTopActivity().getString(R.string.login_expired),
                        ActivityUtils.getTopActivity().getString(R.string.please_relogin)
                    )
                        .setCancelable(false)
                        .setOnOkButtonClickListener { baseDialog, v ->
                            CommonFunction.loginOut(ActivityUtils.getTopActivity())
                            false
                        }
                }
                else -> {
                    onResult.invoke(it)
                }
            }
            view?.hideLoading()
        }, {
            view?.hideLoading()
            loadingDialog?.dismiss()
            view?.showNetError(ExceptionHandle.handleException(it))
        })
}

fun <T : BaseBean> Observable<T>.startAndResult(
    view: IView? = null,
    isShowLoading: Boolean = false,
    onResult: (T) -> Unit,
    onStart: () -> Unit
): Disposable {
    if (isShowLoading) view?.showLoading()
    return this.compose(SchedulerUtils.ioToMain())
        .retryWhen(RetryWithDelay())
        .subscribe({
            when (it.code) {
                ErrorStatus.TOKEN_INVALID -> {
                    // Token 过期，重新登录
                    // Token 过期，重新登录
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        ActivityUtils.getTopActivity().getString(R.string.login_expired),
                        ActivityUtils.getTopActivity().getString(R.string.please_relogin)
                    )
                        .setCancelable(false)
                        .setOnOkButtonClickListener { baseDialog, v ->
                            CommonFunction.loginOut(ActivityUtils.getTopActivity())
                            false
                        }
                }
                else -> {
                    onResult.invoke(it)
                }
            }
            view?.hideLoading()
        }, {
            view?.hideLoading()
            view?.showNetError(ExceptionHandle.handleException(it))
        }, {}, {
            onStart.invoke()
        })
}

