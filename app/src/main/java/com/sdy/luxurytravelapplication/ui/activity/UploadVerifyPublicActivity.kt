package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.tools.SdkVersionUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityUploadVerifyPublicBinding
import com.sdy.luxurytravelapplication.ext.onTakePhoto
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyPublicContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetUploadBean
import com.sdy.luxurytravelapplication.mvp.presenter.UploadVerifyPublicPresenter
import com.sdy.luxurytravelapplication.ui.adapter.SweetNormalPicAdapter
import com.sdy.luxurytravelapplication.ui.adapter.SweetVerifyPicAdapter
import com.sdy.luxurytravelapplication.ui.adapter.VerifyNormalAdapter
import com.zhpan.bannerview.BannerViewPager
import org.jetbrains.anko.startActivity

/**
 * 提交公开照片
 */
class UploadVerifyPublicActivity :
    BaseMvpActivity<UploadVerifyPublicContract.View, UploadVerifyPublicContract.Presenter, ActivityUploadVerifyPublicBinding>(),
    UploadVerifyPublicContract.View, View.OnClickListener {
    private val type by lazy { intent.getIntExtra("type", 1) }

    companion object {
        const val MAX_COUNT = 6
        const val REQUEST_SQUARE_PIC = 1002

        fun startVerifyPublic(context: Context, type: Int) {
            context.startActivity<UploadVerifyPublicActivity>("type" to type)
        }
    }

    override fun createPresenter(): UploadVerifyPublicContract.Presenter =
        UploadVerifyPublicPresenter()


    private val adappter by lazy { SweetVerifyPicAdapter() }
    private val normalPicBottomAdapter by lazy { SweetNormalPicAdapter() }

    override fun initData() {
        when (type) {
            ChooseVerifyActivity.TYPE_HOUSE -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_house)
                binding.uploadType.hint = getString(R.string.sweet_rich_to_be_friend)
            }
            ChooseVerifyActivity.TYPE_CAR -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_car)
                binding.uploadType.hint = getString(R.string.sweet_car_to_be_friend)

            }
            ChooseVerifyActivity.TYPE_FIGURE -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_figure)
                binding.uploadType.hint = getString(R.string.sweet_education_rich)

            }
            ChooseVerifyActivity.TYPE_JOB -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_job)
                binding.uploadType.hint = getString(R.string.sweet_funny_job)

            }

        }
    }

    override fun initView() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.actionbarTitle.text = "身材认证"
            barCl.divider.isVisible = true
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.text = "提交"
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            barCl.rightTextBtn.isEnabled = false
            ClickUtils.applySingleDebouncing(
                arrayOf<View>(
                    barCl.btnBack,
                    normalCloseBtn,
                    barCl.rightTextBtn
                ),
                this@UploadVerifyPublicActivity
            )

            publicUploadRv.layoutManager =
                LinearLayoutManager(this@UploadVerifyPublicActivity, RecyclerView.HORIZONTAL, false)
            publicUploadRv.adapter = adappter

            publicNormalBottomRv.layoutManager =
                LinearLayoutManager(this@UploadVerifyPublicActivity, RecyclerView.HORIZONTAL, false)
            publicNormalBottomRv.adapter = normalPicBottomAdapter
            normalPicBottomAdapter.setNewInstance(UserManager.tempDatas)


            (normalIconRv as BannerViewPager<String>).apply {
                adapter = VerifyNormalAdapter(object : VerifyNormalAdapter.CloseCallBack {
                    override fun close() {
                        BarUtils.setStatusBarColor(this@UploadVerifyPublicActivity, Color.WHITE)
                        binding.normalIconLl.isVisible = false
                    }
                })
                setRevealWidth(SizeUtils.dp2px(22F))
                setPageMargin(SizeUtils.dp2px(15F))
                create(UserManager.tempDatas)
            }
        }
        adappter.addData(SweetUploadBean())
        adappter.addChildClickViewIds(R.id.sweetPicDelete)
        adappter.setOnItemClickListener { _, view, position ->
            if (adappter.data[position].url.isEmpty()) {
                onTakePhoto(
                    this@UploadVerifyPublicActivity, MAX_COUNT - (adappter.data.size - 1),
                    REQUEST_SQUARE_PIC, compress = true
                )
            }
        }
        adappter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.sweetPicDelete -> {
                    adappter.removeAt(position)
                    if (adappter.data.size < MAX_COUNT + 1
                        && !adappter.data.contains(SweetUploadBean())
                    ) {
                        adappter.addData(SweetUploadBean())
                    }
                    binding.barCl.rightTextBtn.isEnabled = adappter.data.size > 1
                }
            }
        }

        normalPicBottomAdapter.setOnItemClickListener { _, view, position ->
            binding.normalIconLl.isVisible = true
            BarUtils.setStatusBarColor(this, Color.parseColor("#B3000000"))
            binding.normalIconRv.setCurrentItem(position, true)

        }


    }

    override fun start() {

    }

    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.barCl.rightTextBtn -> {
                startActivity<SweetHeartVerifyingActivity>()
//                mPresenter.uploadPhoto(adappter.data[index].url, index)
            }
            binding.normalCloseBtn -> {
                BarUtils.setStatusBarColor(this, Color.WHITE)
                binding.normalIconLl.isVisible = false
            }
//            binding. seeUploadNormalBtn -> {
//                BarUtils.setStatusBarColor(this, Color.parseColor("#B3000000"))
//                binding.normalIconLl.isVisible = true
//            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SQUARE_PIC) {
            if (data != null) {
                if (!PictureSelector.obtainMultipleResult(data).isNullOrEmpty()) {
                    for (tdata in PictureSelector.obtainMultipleResult(data)) {
                        if (SdkVersionUtils.checkedAndroid_Q() && !tdata.androidQToPath.isNullOrEmpty()) {
                            adappter.addData(
                                adappter.data.size - 1,
                                SweetUploadBean(
                                    0,
                                    0,
                                    tdata.androidQToPath,
                                    tdata.width,
                                    tdata.height
                                )
                            )
                        } else {
                            adappter.addData(
                                adappter.data.size - 1,
                                SweetUploadBean(
                                    0, 0,
                                    if (tdata.compressPath.isNotEmpty()) {
                                        tdata.compressPath
                                    } else {
                                        tdata.path
                                    }, tdata.width, tdata.height
                                )

                            )
                        }
                    }
                    binding.barCl.rightTextBtn.isEnabled = adappter.data.size > 1
                    if (adappter.data.size - 1 == MAX_COUNT) {
                        adappter.remove(adappter.data.size - 1)
                    }
                }
            }


        }
    }

}