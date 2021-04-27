package com.sdy.luxurytravelapplication.base

import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.sdy.luxurytravelapplication.ui.dialog.LoadingDialog

/**
 * @author chenxz
 * @date 2018/9/7
 * @desc BaseMvpActivity
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMvpActivity<in V : IView, P : IPresenter<V>, VB : ViewBinding> :
    BaseActivity<VB>(), IView {

    /**
     * Presenter
     */
    protected var mPresenter: P? = null

    protected var mPresenterDetach = true

    protected abstract fun createPresenter(): P

    override fun initView() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPresenterDetach) {
            mPresenter?.detachView()
            this.mPresenter = null
        }
    }

    private val loading by lazy {
        LoadingDialog()

    }

    override fun showLoading() {
//        loading.showNoAutoDismiss()
        loading.show()
    }

    override fun hideLoading() {
//        loading.doDismiss()
        loading.dismiss()
    }

    override fun showError(errorMsg: String) {
        ToastUtils.showShort(errorMsg)
    }

    override fun showNetError(msg: String) {
        ToastUtils.showShort(msg)
    }

    override fun showMsg(msg: String) {
        ToastUtils.showShort(msg)
    }


}