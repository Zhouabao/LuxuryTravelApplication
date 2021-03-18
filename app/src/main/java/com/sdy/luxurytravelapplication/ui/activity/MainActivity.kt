package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.os.Bundle
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityMainBinding
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.presenter.MainPresenter
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class MainActivity :
    BaseMvpActivity<MainContract.View, MainContract.Presenter, ActivityMainBinding>(),
    MainContract.View {
    companion object {
        fun startToMain(context: Context, clearTop: Boolean = true) {
            if (clearTop)
                context.startActivity(context.intentFor<MainActivity>().clearTask().newTask())
            else
                context.startActivity(context.intentFor<MainActivity>())
        }
    }
    override fun createPresenter(): MainContract.Presenter = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
    }

    override fun initView() {
    }

    override fun start() {
    }

    override fun showLogoutSuccess(success: Boolean) {

    }

    override fun showUserInfo(bean: Any) {

    }

}