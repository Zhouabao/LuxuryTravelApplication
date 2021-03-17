package com.sdy.luxurytravelapplication.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils

/**
 * @author chenxz
 * @date 2018/9/7
 * @desc BaseMvpFragment
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMvpFragment<in V : IView, P : IPresenter<V>, VB : ViewBinding> :
    BaseFragment<VB>(), IView {

    /**
     * Presenter
     */
    protected var mPresenter: P? = null

    protected abstract fun createPresenter(): P

    override fun initView(view: View) {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.detachView()
        this.mPresenter = null
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showError(errorMsg: String) {
        ToastUtils.showShort(errorMsg)
    }

    override fun showDefaultMsg(msg: String) {
        ToastUtils.showShort(msg)
    }

    override fun showMsg(msg: String) {
        ToastUtils.showShort(msg)
    }

}