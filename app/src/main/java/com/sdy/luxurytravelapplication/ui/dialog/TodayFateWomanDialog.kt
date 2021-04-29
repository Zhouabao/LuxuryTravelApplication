package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogTodayFateWomanBinding
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TodayFateBean
import com.sdy.luxurytravelapplication.nim.attachment.ChatUpAttachment
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.util.RecyclerViewUtil
import com.sdy.luxurytravelapplication.ui.adapter.FateAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class TodayFateWomanDialog(
    val nearBean: IndexRecommendBean,
    val data: TodayFateBean
) : BaseBindingDialog<DialogTodayFateWomanBinding>(
    SizeUtils.dp2px(300F),
    WindowManager.LayoutParams.WRAP_CONTENT,
    R.style.MyDialogCenterAnimation,
    true
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initWindow()
        initView()
    }

    private val adapter by lazy { FateAdapter() }

//    private fun initWindow() {
//        val window = this.window
//        window?.setGravity(Gravity.CENTER)
//        val params = window?.attributes
//        params?.width = SizeUtils.dp2px(300F)
//        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        params?.windowAnimations = R.style.MyDialogCenterAnimation
//        window?.attributes = params
//        setCanceledOnTouchOutside(true)
//    }

    private fun initView() {
        binding.apply {
            t2.text = context.getString(
                R.string.hurry_give,

                if (UserManager.gender == 1) {
                    context.getString(R.string.she)
                } else {
                    context.getString(R.string.he)
                }
            )

            rvFate.layoutManager = GridLayoutManager(context, 3)
            rvFate.adapter = adapter


            //全部默认选中
            for (data in data.list) {
                data.checked = true
            }
            adapter.setNewInstance(data.list)
            checkFateEnable()

            RecyclerViewUtil.changeItemAnimation(rvFate, false)
            adapter.setOnItemClickListener { _, view, position ->
                adapter.data[position].checked = !adapter.data[position].checked

                checkFateEnable()
                adapter.notifyItemChanged(position)
            }


            closeBtn.setOnClickListener {
                dismiss()
            }
            ClickUtils.applySingleDebouncing(hiFateBtn) {
                batchGreet()
            }
        }
    }

    private fun checkFateEnable() {
        for (data in adapter.data) {
            if (data.checked) {
                binding.hiFateBtn.isEnabled = true
                break
            } else {
                binding.hiFateBtn.isEnabled = false
            }
        }
    }


    /**
     *
     */
    fun batchGreet() {
        val loadingDialog = LoadingDialog()
        val ids = mutableListOf<String>()
        for (data in adapter.data) {
            if (data.checked) {
                ids.add(data.accid)
            }
        }
        val params = hashMapOf<String, Any>()
        params["batch_accid"] = Gson().toJson(ids)
        RetrofitHelper.service
            .batchGreetWoman(params)
            .ssss (loadingDialog = loadingDialog){ t ->
                if (t.code == 200 && !t.data.isNullOrEmpty()) {
                    for (data in t.data.withIndex()) {
                        if (!data.value.msg.isEmpty()) {
                            //随机发送一条搭讪语消息
                            val chatUpAttachment = ChatUpAttachment(data.value.msg)
                            val msg = MessageBuilder.createCustomMessage(
                                data.value.accid,
                                SessionTypeEnum.P2P,
                                chatUpAttachment
                            )


                            NIMClient.getService(MsgService::class.java).sendMessage(msg, false)
                                .setCallback(object : RequestCallback<Void> {
                                    override fun onSuccess(p0: Void?) {
                                        if (data.index == t.data.size - 1) {
                                            loadingDialog.dismiss()
                                            dismiss()
                                            ToastUtil.toast(context.getString(R.string.send_hi_success))
                                        }
                                    }

                                    override fun onFailed(p0: Int) {
                                    }

                                    override fun onException(p0: Throwable?) {
                                    }

                                })
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                }
            }

    }


    override fun dismiss() {
        super.dismiss()
        if (!UserManager.showCompleteUserCenterDialog) {
            if (!nearBean.today_pull_share) {
                //如果自己的完善度小于标准值的完善度，就弹出完善个人资料的弹窗
                InviteFriendDialog().show()
            } else if (!nearBean.today_pull_dating) {
                PublishDatingDialog().show()
            }
        }
        UserManager.showIndexRecommend = true

    }
}
