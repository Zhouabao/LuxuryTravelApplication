package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils.isActivityExistsInStack
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemLayoutTagSquareBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareTagBean
import com.sdy.luxurytravelapplication.ui.activity.TagDetailCategoryActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import org.jetbrains.anko.padding
import org.jetbrains.anko.startActivity

class TagSquareAdapter(var spanCnt: Int=3) :
    BaseBindingQuickAdapter<SquareTagBean, ItemLayoutTagSquareBinding>(R.layout.item_layout_tag_square) {
    override fun convert(
        binding: ItemLayoutTagSquareBinding,
        position: Int,
        item: SquareTagBean
    ) {
        binding.apply {

            val params = root.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = SizeUtils.dp2px(8F)
            params.rightMargin = SizeUtils.dp2px(8F)
            root.padding = SizeUtils.dp2px(17F)

            rvTagSquareImg.layoutManager = GridLayoutManager(context, spanCnt)
            val adapter = TagSquarePicAdapter(spanCnt)
            adapter.addData(item.child)

            rvTagSquareImg.setOnTouchListener { _, event -> root.onTouchEvent(event) }
            adapter.setOnItemClickListener { _, view, position ->
//                if (UserManager.touristMode) {
//                    TouristDialog(context).show()
//                } else
                    if (!isActivityExistsInStack(TagDetailCategoryActivity::class.java))
                        context.startActivity<TagDetailCategoryActivity>(
                            "id" to item.id,
                            "type" to TagDetailCategoryActivity.TYPE_TAG
                        )

            }


            rvTagSquareImg.adapter = adapter
            GlideUtil.loadCircleImg(context, item.icon, tagImg)
            tagName.text = item.title
            tagIsHot.text = "645人参与·5555次浏览"
            ClickUtils.applySingleDebouncing(btnTagMore) {

            }
        }
    }
}