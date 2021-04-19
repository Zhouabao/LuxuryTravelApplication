package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSendGiftRvBinding
import com.sdy.luxurytravelapplication.event.UpdateChatCallGiftEvent
import com.sdy.luxurytravelapplication.mvp.model.bean.GiftBean
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import org.greenrobot.eventbus.EventBus

class SendGiftBannerAdapter :
    BaseBannerAdapter<List<GiftBean>>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_send_gift_rv
    }

    override fun bindData(
        holder: BaseViewHolder<List<GiftBean>>,
        item: List<GiftBean>,
        parentPosition: Int,
        pageSize: Int
    ) {
        val binding = ItemSendGiftRvBinding.bind(holder.itemView)
        binding.apply {

            sendGiftRv.layoutManager =
                GridLayoutManager(binding.root.context, 4, RecyclerView.VERTICAL, false)
            val adapter = SendGiftAdapter(true)
            sendGiftRv.adapter = adapter

            adapter.setOnItemClickListener { _, view, position ->
                EventBus.getDefault()
                    .post(UpdateChatCallGiftEvent(adapter.data[position], parentPosition, position))
            }

            adapter.addData(item)
        }

    }


}

