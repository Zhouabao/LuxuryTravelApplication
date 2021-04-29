package com.sdy.luxurytravelapplication.ui.adapter

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.VibrateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.duluduludala.xkvideo.base.config.GlideSmartRoundedCornersTransform
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemFindRecommendBinding
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareBean
import com.sdy.luxurytravelapplication.ui.activity.SquareCommentDetailActivity
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.BlurTransformation

class RecommendSquareAdapter(var mine: Boolean = false) :
    BaseBindingQuickAdapter<RecommendSquareBean, ItemFindRecommendBinding>(R.layout.item_find_recommend) {
    override fun convert(
        binding: ItemFindRecommendBinding,
        position: Int,
        item: RecommendSquareBean
    ) {
        binding.apply {
//            val params = root.layoutParams as RecyclerView.LayoutParams
//            params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(37F)) / 2
//            params.leftMargin = SizeUtils.dp2px(7F)
//            (squareImg.layoutParams as ConstraintLayout.LayoutParams).width=params.width
            (squareImg.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                if (item.type == 0 || item.type == 3) { //0纯文本和3语音是1:1
                    "1:1"
                } else { //1图片 2视频
                    when {
                        item.width > item.height -> "4:3"
                        item.width < item.height -> "3:4"
                        else -> "1:1"
                    }
                }


            if (item.approve_type != 0) {
                squareDistanceLl.isVisible = false
                squareSweet.isVisible = true
                squareSweetLogo.isVisible = true

                //0普通动态 1 资产 2豪车 3身材 4职业
                squareSweet.text = when (item.approve_type) {
                    1 -> {
                        context.getString(R.string.sweet_wealth_title)
                    }
                    2 -> {
                        context.getString(R.string.sweet_luxury_car_title)
                    }
                    3 -> {
                        context.getString(R.string.sweet_figure_title)
                    }
                    4 -> {
                        context.getString(R.string.sweet_job_title)
                    }
                    6 -> {
                        context.getString(R.string.sweet_education_title)
                    }
                    else -> {
                        ""
                    }
                }


                squareContent.setTextColor(Color.parseColor("#FFFC9010"))
            } else {
                squareSweet.isVisible = false
                squareSweetLogo.isVisible = false
                if (!item.is_elite) {
                    squareDistanceLl.isVisible = !item.distance.isNullOrEmpty()
                    squareDistance.text = "${item.distance}"
                } else {
                    squareDistanceLl.isVisible = true
                    squareDistance.text = context.getString(R.string.recommend)
                }
            }


            if (item.type == 0) {//纯文本
                squareImg.visibility = View.INVISIBLE
                squareAudioCover.isVisible = false
                squareOnlyTextContent.isVisible = true
                squareVideoState.isVisible = false

                squareOnlyTextContent.text = "${item.descr}"
            } else if (item.type == 1) {//图片
                squareOnlyTextContent.isVisible = false
                squareImg.isVisible = true
                squareAudioCover.isVisible = false
                squareVideoState.isVisible = false
                GlideUtil.loadImg(
                    context,
                    item.cover_url,
                    squareImg
                )
            } else if (item.type == 2) {//2视频
                squareOnlyTextContent.isVisible = false
                squareImg.isVisible = true
                squareAudioCover.isVisible = false
                squareVideoState.isVisible = true
                squareVideoState.setImageResource(R.drawable.icon_play_transparent)
                GlideUtil.loadImg(
                    context,
                    item.cover_url,
                    squareImg
                )
            } else if (item.type == 3) {//语音
                squareOnlyTextContent.isVisible = false
                squareImg.isVisible = true
                squareAudioCover.isVisible = true
                squareVideoState.isVisible = false

                val multiTransformation = MultiTransformation(
                    CenterCrop(),
                    GlideSmartRoundedCornersTransform(SizeUtils.dp2px(15F), 0),
                    BlurTransformation(SizeUtils.dp2px(15F))
                )
                Glide.with(context)
                    .load(item.avatar)
                    .priority(Priority.LOW)
                    .thumbnail(0.5F)
                    .transform(multiTransformation)
                    .into(squareImg)
            }



            squareContent.isVisible = !item.descr.isNullOrEmpty() && item.type != 0
            squareContent.text = "${item.descr}"
            squareLike.text = "${item.like_cnt}"
            squareName.text = item.nickname
            GlideUtil.loadCircleImg(context, item.avatar, squareAvator)
            //设置点赞状态
            setLikeStatus(
                item.isliked,
                item.like_cnt,
                clickZanViewAni,
                squareLike, false
            )

            clickZanViewAni.setOnClickListener {
//                ToastUtil.toast("喜欢")
//                if (UserManager.touristMode) {
//                    TouristDialog(context).show()
//                } else
                    if (item.accid != UserManager.accid) {
                        clickZan(
                            clickZanViewAni,
                            squareLike,
                            position - headerLayoutCount
                        )
                    }
            }
//         squareLike.onClick {
//            if (item.accid != UserManager.getAccid()) {
//                clickZan(
//                     clickZanViewImg,
//                     clickZanViewAni,
//                     squareLike,
//                    helper.layoutPosition - headerLayoutCount
//                )
//            }
//        }

            //点击跳转
            root.setOnClickListener {
                SquareCommentDetailActivity.start(
                    context,
                    squareId = item.id,
                    position = position,
                    type = if (item.approve_type != 0) {
                        SquareCommentDetailActivity.TYPE_SWEET
                    } else {
                        SquareCommentDetailActivity.TYPE_SQUARE
                    },
                    gender = item.gender
                )

            }

            squareAvator.setOnClickListener {
                TargetUserActivity.start(context, item.accid)
            }
        }
    }

    /**
     * 设置点赞状态
     */
    private fun setLikeStatus(
        isliked: Boolean,
        likeCount: Int,
        likeAnim: LottieAnimationView,
        likeView: TextView,
        animate: Boolean = true
    ) {
        likeAnim.isVisible = true
        if (isliked) {
            if (animate) {
                likeAnim.playAnimation()
                VibrateUtils.vibrate(50L)
            } else {
                likeAnim.progress = 1F
            }
        } else {
            likeAnim.progress = 0F
        }

        likeView.text = "${if (likeCount < 0) {
            0
        } else {
            likeCount
        }}"
    }


    /**
     * 点赞按钮
     */
    private fun clickZan(
        likeAnim: LottieAnimationView,
        likeBtn: TextView,
        position: Int
    ) {
        if (data[position].isliked) {
            data[position].isliked = !data[position].isliked
            data[position].like_cnt = data[position].like_cnt!!.minus(1)
        } else {
            data[position].isliked = !data[position].isliked
            data[position].like_cnt = data[position].like_cnt!!.plus(1)
        }
        setLikeStatus(data[position].isliked, data[position].like_cnt, likeAnim, likeBtn)

        likeBtn.postDelayed({
            if (data.isEmpty() || data.size - 1 < position)
                return@postDelayed
            if (data[position].originalLike == data[position].isliked) {
                return@postDelayed
            }
            val params = hashMapOf<String, Any>(
                "type" to if (!data[position].isliked) {
                    2
                } else {
                    1
                },
                "square_id" to data[position].id!!
            )
            getSquareLike(params, position)
        }, 500L)


    }


    /**
     * 点赞 取消点赞
     * 1 点赞 2取消点赞
     */
    fun getSquareLike(params: HashMap<String, Any>, position: Int) {
        RetrofitHelper.service.getSquareLike(params).ssss(null, false) {
            if (it.code == 200) {
                onGetSquareLikeResult(position, true)
            } else if (it.code == 403) {
                UserManager.startToLogin(context as Activity)
            } else {
                onGetSquareLikeResult(position, false)
            }
        }

    }


    /**
     * 点赞结果
     */
    private fun onGetSquareLikeResult(position: Int, success: Boolean) {
        if (success) {
            data[position].originalLike = data[position].isliked
        } else {
            data[position].isliked = data[position].originalLike
            data[position].like_cnt = data[position].originalLikeCount
//            refreshNotifyItemChanged(position)

        }
    }

}