package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemMyCommentBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MyCommentBean
import com.sdy.luxurytravelapplication.ui.activity.SquareCommentDetailActivity
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class MyCommentAdapter :
    BaseBindingQuickAdapter<MyCommentBean, ItemMyCommentBinding>(R.layout.item_my_comment) {
    override fun convert(binding: ItemMyCommentBinding, position: Int, item: MyCommentBean) {
        binding.apply {

            GlideUtil.loadCircleImg(context, UserManager.avatar, footIcon)
            footContent.text = item.content
            footNickname.text = UserManager.nickname
            footTime.text = item.create_time
            findContent.text = item.square_descr
            findUserName.text = item.nickname
            when (item.type) {
                1 -> {//图片
                    findIconType.isVisible = false
                    GlideUtil.loadRoundImgCenterCrop(
                        context,
                        item.cover_url,
                        findIcon,
                        SizeUtils.dp2px(10F)
                    )
                }
                2 -> {//视频

                    GlideUtil.loadRoundImgCenterCrop(
                        context,
                        item.cover_url,
                        findIcon,
                        SizeUtils.dp2px(10F)
                    )
                    findIconType.isVisible = true
                    findIconType.setImageResource(R.drawable.icon_play_transparent)
                }
                3 -> {//语音
                    findIcon.setImageResource(R.drawable.gradient_purple_10dp)
                    findIconType.isVisible = true
                    findIconType.setImageResource(R.drawable.icon_voice_white)

                }
                else -> { //0文字
                    findIconType.isVisible = false
                    findIcon.isVisible = false
                }
            }
            ClickUtils.applySingleDebouncing(root) {
                SquareCommentDetailActivity.start(
                    context,
                    squareId = item.square_id
                )
            }
            ClickUtils.applySingleDebouncing(footIcon) {
                TargetUserActivity.start(context, item.accid)
            }
        }

    }

}
