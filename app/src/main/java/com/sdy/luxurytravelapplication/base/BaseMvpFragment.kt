package com.sdy.luxurytravelapplication.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.v3.WaitDialog

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

    private val loading by lazy {
        WaitDialog.build(activity as AppCompatActivity).setTheme(
            DialogSettings.THEME.LIGHT
        )
    }
    override fun showLoading() {
        loading.showNoAutoDismiss()
    }

    override fun hideLoading() {
        loading.doDismiss()
    }
    override fun showError(errorMsg: String) {
        mLayoutStatusView?.showError()
    }

    override fun showDefaultMsg(msg: String) {
        ToastUtils.showShort(msg)
    }

    override fun showMsg(msg: String) {
        ToastUtils.showShort(msg)
    }

}