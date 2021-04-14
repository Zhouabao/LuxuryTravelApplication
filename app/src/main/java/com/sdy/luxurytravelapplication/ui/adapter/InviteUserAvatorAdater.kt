package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemUserCenterVisitCoverBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.Invite
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.BlurTransformation

class InviteUserAvatorAdater :
    BaseBindingQuickAdapter<Invite, ItemUserCenterVisitCoverBinding>(R.layout.item_user_center_visit_cover) {
    public var allInviteCount = 0
    override fun convert(binding: ItemUserCenterVisitCoverBinding, position: Int, item: Invite) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.setMargins(if (position != 0) SizeUtils.dp2px(-18F) else 0, 0, 0, 0)
            params.width = SizeUtils.dp2px(36F)
            params.height = SizeUtils.dp2px(36F)
            root.layoutParams = params
            //如果不是会员，就高斯模糊看过我的
            Glide.with(context)
                .load(item.avatar)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(1, 10)))
                .into(visitCoverImg)


            if (allInviteCount > 3 && position == 3) {
                visitCnt.isVisible = true
                visitCnt.text = "+$allInviteCount"
                visitCoverImg.isInvisible = true
            }
        }

    }

}
