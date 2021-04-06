package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSquareMessageBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareMsgBean
import com.sdy.luxurytravelapplication.ui.activity.SquareCommentDetailActivity
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.ui.fragment.SquareZanFragment
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2019/8/511:22
 *    desc   :
 *     //类型 1，广场点赞 2，评论我的 3。我的评论点赞的 4 @我的
 *     //发布消息的类型 0,纯文本的 1，照片 2，视频 3，声音
 *    version: 1.0
 */
class MessageSquareAdapter(val type: Int = SquareZanFragment.TYPE_ZAN) :
    BaseBindingQuickAdapter<SquareMsgBean, ItemSquareMessageBinding>(R.layout.item_square_message) {
    override fun convert(binding: ItemSquareMessageBinding, position: Int, item: SquareMsgBean) {
        binding.apply {

            GlideUtil.loadCircleImg(context, item.avatar, squareAvator)
            squareNickname.text = item.nickname

            if (type == SquareZanFragment.TYPE_ZAN)
                squareContent.text = context.getString(R.string.zan_your_square)
            else
                SpanUtils.with(squareContent)
                    .append(context.getString(R.string.comment)).append(item.content)
                    .setForegroundColor(context.resources.getColor(R.color.color333))
                    .create()

            //类型0 文本1图片 2语音 3视频
            // //type 发布消息的类型0,纯文本的 1，照片 2，视频 3，声音
            when (item.category) {
                0 -> {
                    squareTypeVideo.isVisible = false
                    squareIcon.setImageResource(R.drawable.default_image_10dp)
                    squareTypeText.isVisible = true
                    squareTypeText.text = item.descr

                }
                1 -> {
                    squareTypeVideo.isVisible = false
                    GlideUtil.loadRoundImgCenterCrop(
                        context,
                        item.cover_url,
                        squareIcon,
                        SizeUtils.dp2px(10F)
                    )
                    squareTypeText.isVisible = false
                }
                2 -> {
                    squareTypeVideo.isVisible = true
                    squareTypeVideo.setImageResource(R.drawable.icon_play_transparent)
                    GlideUtil.loadRoundImgCenterCrop(
                        context,
                        item.cover_url,
                        squareIcon,
                        SizeUtils.dp2px(10F)
                    )
                    squareTypeText.isVisible = false
                }
                3 -> {
                    squareTypeVideo.isVisible = true
                    squareTypeVideo.setImageResource(R.drawable.icon_voice_white)
                    squareIcon.setImageResource(R.drawable.gradient_purple_10dp)
                    squareTypeText.isVisible = false

                }
            }

            squareAvator.setOnClickListener {
                TargetUserActivity.start(context, item.accid)
            }

            root.setOnClickListener {
                SquareCommentDetailActivity.start(context, squareId = item.id)
            }
        }
    }

}
