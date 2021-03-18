package com.sdy.luxurytravelapplication.ui.activity

import android.os.Bundle
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityMainBinding
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.presenter.MainPresenter

class MainActivity :
    BaseMvpActivity<MainContract.View, MainContract.Presenter, ActivityMainBinding>(),
    MainContract.View {
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