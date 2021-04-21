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
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.tools.SdkVersionUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityUploadVerifyPublicBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyPublicContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaParamBean
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


    private val keys = arrayListOf<MediaParamBean>()
    private var index = 0
    private val adapter by lazy { SweetVerifyPicAdapter() }
    private val normalPicBottomAdapter by lazy { SweetNormalPicAdapter() }
    override fun start() {
        mPresenter?.getPicTpl(type)
    }

    override fun initData() {
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
            publicUploadRv.adapter = adapter

            publicNormalBottomRv.layoutManager =
                LinearLayoutManager(this@UploadVerifyPublicActivity, RecyclerView.HORIZONTAL, false)
            publicNormalBottomRv.adapter = normalPicBottomAdapter


            (normalIconRv as BannerViewPager<String>).apply {
                adapter = VerifyNormalAdapter(object : VerifyNormalAdapter.CloseCallBack {
                    override fun close() {
                        BarUtils.setStatusBarColor(this@UploadVerifyPublicActivity, Color.WHITE)
                        binding.normalIconLl.isVisible = false
                    }
                })
                setRevealWidth(SizeUtils.dp2px(22F))
                setPageMargin(SizeUtils.dp2px(15F))
                create()
            }
        }
        adapter.addData(SweetUploadBean())
        adapter.addChildClickViewIds(R.id.sweetPicDelete)
        adapter.setOnItemClickListener { _, view, position ->
            if (adapter.data[position].url.isEmpty()) {
                CommonFunction.onTakePhoto(
                    this@UploadVerifyPublicActivity, MAX_COUNT - (adapter.data.size - 1),
                    REQUEST_SQUARE_PIC, compress = true
                )
            }
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.sweetPicDelete -> {
                    adapter.removeAt(position)
                    if (adapter.data.size < MAX_COUNT + 1
                        && !adapter.data.contains(SweetUploadBean())
                    ) {
                        adapter.addData(SweetUploadBean())
                    }
                    binding.barCl.rightTextBtn.isEnabled = adapter.data.size > 1
                }
            }
        }

        normalPicBottomAdapter.setOnItemClickListener { _, view, position ->
            binding.normalIconLl.isVisible = true
            BarUtils.setStatusBarColor(this, Color.parseColor("#B3000000"))
            binding.normalIconRv.setCurrentItem(position, true)

        }



        when (type) {
            ChooseVerifyActivity.TYPE_HOUSE -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_house)
                binding.uploadType.hint = getString(R.string.sweet_rich_to_be_friend)
            }
            ChooseVerifyActivity.TYPE_CAR -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_car)
                binding.uploadType.hint = getString(R.string.sweet_car_to_be_friend)

            }
            ChooseVerifyActivity.TYPE_EDUCATION -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_education)
                binding.uploadType.hint = getString(R.string.sweet_education_rich)

            }
            ChooseVerifyActivity.TYPE_JOB -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_job)
                binding.uploadType.hint = getString(R.string.sweet_funny_job)

            }

        }
    }


    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.barCl.rightTextBtn -> {
                mPresenter?.uploadPhoto(adapter.data[index].url, index)
            }
            binding.normalCloseBtn -> {
                BarUtils.setStatusBarColor(this, Color.WHITE)
                binding.normalIconLl.isVisible = false
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SQUARE_PIC) {
            if (data != null) {
                if (!PictureSelector.obtainMultipleResult(data).isNullOrEmpty()) {
                    for (tdata in PictureSelector.obtainMultipleResult(data)) {
                        if (SdkVersionUtils.checkedAndroid_Q() && !tdata.androidQToPath.isNullOrEmpty()) {
                            adapter.addData(
                                adapter.data.size - 1,
                                SweetUploadBean(
                                    0,
                                    0,
                                    tdata.androidQToPath,
                                    tdata.width,
                                    tdata.height
                                )
                            )
                        } else {
                            adapter.addData(
                                adapter.data.size - 1,
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
                    binding.barCl.rightTextBtn.isEnabled = adapter.data.size > 1
                    if (adapter.data.size - 1 == MAX_COUNT) {
                        adapter.removeAt(adapter.data.size - 1)
                    }
                }
            }


        }
    }

    override fun uploadImgResult(success: Boolean, key: String, index1: Int) {
        if (success) {
            keys.add(
                MediaParamBean(
                    key,
                    0,
                    adapter.data[index1].width,
                    adapter.data[index1].height
                )
            )
            var size = 0
            for (data in adapter.data) {
                if (data.url.isNotEmpty()) {
                    size++
                }
            }
            if (index == size - 1) {
                mPresenter?.uploadData(
                    2,
                    type,
                    Gson().toJson(keys),
                    binding.uploadType.text.trim().toString()
                )
            } else {
                index++
                mPresenter?.uploadPhoto(adapter.data[index].url, index)
            }
        } else {
            index = 0
            keys.clear()
        }
    }

    override fun uploadDataResult(success: Boolean) {
        if (success) {
            SweetHeartVerifyingActivity.start(this, type)
        }
    }

    override fun getPicTplResult(datas: ArrayList<String>) {
        normalPicBottomAdapter.setNewInstance(datas)
        binding.normalIconRv.refreshData(datas)
    }


}