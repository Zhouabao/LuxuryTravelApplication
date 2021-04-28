package com.sdy.luxurytravelapplication.ui.activity

import com.blankj.utilcode.util.ScreenUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityBigImageBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoJson
import com.sdy.luxurytravelapplication.ui.adapter.SquareDetailImgsAdaper
import com.zhpan.bannerview.BannerViewPager

/**
 * 点击图片实现查看大图
 */
class BigImageActivity : BaseActivity<ActivityBigImageBinding>() {
    companion object {
        val IMG_KEY = "squareBean"
        val IMG_POSITION = "imagepostion"
        val IMG_CURRENT_POSITION = "image_current_postion"

    }

    private val squareBean: SquareBean by lazy { intent.getSerializableExtra(IMG_KEY) as SquareBean }
    private val currIndex by lazy { intent.getIntExtra(IMG_POSITION, 0) }


    override fun initData() {
    }

    override fun initView() {
        ScreenUtils.setFullScreen(this)
        binding.apply {
            (bigImageVP as BannerViewPager<VideoJson>).apply {
                adapter = SquareDetailImgsAdaper()
            }.create()

            bigImageVP.refreshData(squareBean.photo_json)
            bigImageVP.setCurrentItem(currIndex, false)
            bigImageVP.setOnClickListener {
                finish()
            }
        }
    }

    override fun start() {
    }

}