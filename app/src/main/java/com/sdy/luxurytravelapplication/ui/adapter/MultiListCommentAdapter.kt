package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemCommentParentBinding
import com.sdy.luxurytravelapplication.databinding.LayoutCommentHeadBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.CommentBean

class MultiListCommentAdapter : BaseMultiItemQuickAdapter<CommentBean, BaseViewHolder>() {
    init {
        addItemType(CommentBean.TITLE, R.layout.layout_comment_head)
        addItemType(CommentBean.CONTENT, R.layout.item_comment_parent)
    }


    override fun convert(holder: BaseViewHolder, item: CommentBean) {
        when (holder.itemViewType) {
            CommentBean.TITLE -> {
                val binding = LayoutCommentHeadBinding.bind(holder.itemView)
                binding.allT2.text = item.content ?: ""
            }
            CommentBean.CONTENT -> {
                val binding = ItemCommentParentBinding.bind(holder.itemView)
                if (holder.layoutPosition == data.size - 1) {
                    binding.commentDivider.visibility = View.INVISIBLE
                } else {
                    binding.commentDivider.visibility = View.VISIBLE
                }
                if (item.reply_content.isNullOrEmpty() || item.replyed_nickname.isNullOrEmpty()) {
                    binding.childView.root.visibility = View.GONE
                } else {
                    binding.childView.root.visibility = View.VISIBLE
//                    holder.addOnClickListener(R.id.childView)
                    binding.childView.commentReplyContent.text =
                        SpanUtils.with(binding.childView.commentReplyContent)
                            .append("${item.replyed_nickname}：")
                            .setForegroundColor(context.resources.getColor(R.color.color333))
                            .setBold()
                            .append("${item.reply_content}")
                            .create()
                }

                GlideUtil.loadAvatorImg(context, item.avatar ?: "", binding.commentUser)
                binding.commentUserName.text = item.nickname ?: ""
                binding.commentTime.text = item.create_time ?: ""
                binding.commentContent.text = item.content
                binding.commentDianzanNum.text = "${item.like_count}"
                binding.commentDianzanBtn.setImageResource(
                    if (item.isliked == 1) {
                        R.drawable.icon_zan_comment_checked
                    } else {
                        R.drawable.icon_zan_comment_normal
                    }
                )
            }
        }
    }


}