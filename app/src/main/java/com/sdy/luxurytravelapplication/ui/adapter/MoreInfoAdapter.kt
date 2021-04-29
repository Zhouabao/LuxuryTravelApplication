package com.sdy.luxurytravelapplication.ui.adapter

import android.text.TextUtils.replace
import android.util.Log
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMoreInfoBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.AnswerBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class MoreInfoAdapter :
    BaseBindingQuickAdapter<AnswerBean, ItemMoreInfoBinding>(R.layout.item_more_info) {
    //改变的参数
    public val params by lazy { hashMapOf<String, Any>() }

    override fun convert(binding: ItemMoreInfoBinding, position: Int, item: AnswerBean) {

        binding.apply {
            moreInfoTitle.text = item.title
            moreInfoContent.hint = item.descr
            if (item.title == context.getString(R.string.height) && !item.find_tag!!.title.isNullOrEmpty()) {
                moreInfoContent.text = item.find_tag!!.title
            } else if (item.find_tag != null && item.child.contains(item.find_tag!!) && item.find_tag!!.id != 0) {
                moreInfoContent.text = item.find_tag!!.title
            }
        }
    }

}
