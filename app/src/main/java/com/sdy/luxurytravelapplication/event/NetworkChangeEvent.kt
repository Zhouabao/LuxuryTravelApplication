package com.sdy.luxurytravelapplication.event

import com.netease.nimlib.sdk.msg.model.IMMessage
import java.io.File

/**
 * Created by chenxz on 2018/8/1.
 */
class NetworkChangeEvent(var isConnected: Boolean)


/**
 * 图片保存成功通知
 */
class SaveImgSuccessEvent(val filePath: File)

/**
 * 别人操作了礼物状态，自己这端更新礼物
 */
class RefreshGiftStatusEvent(val giftStatus: Int, val messageId: String)

//更新列表对象移除
class RemoveChatUpEvent(val message: IMMessage)


class RefreshMessageCenterEvent()

/**
 * 更新通讯录（加好友、星标、删除好友、移除星标等均要更新）
 */
class UpdateContactBookEvent

//更新聊天界面的星标状态
class UpdateStarEvent(val isStar: Boolean)


class SavePictureEvent()

class DelSquareMsgEvent(val position: Int)
class UpdateUnreadCntEvent


class SendLikeTipMessageEvent(val accid: String, val isReceive: Boolean)



//更新个人认证
class UpdateVerifyEvent

class RefreshGoldEvent()


//刷新点赞等事件
class RefreshLikeEvent(
    val squareId: Int,
    val isLike: Int,
    val position: Int,
    var likeCount: Int = -1
)



//刷新删除动态事件
class RefreshDeleteSquareEvent(val squareId: Int)


//刷新评论数量
class RefreshCommentEvent(val commentNum: Int, val position: Int)


//刷新事件  local 是否是本地
class RefreshSquareEvent(val refresh: Boolean, var from: String = "")

//根据性别筛选更新数据
class RefreshSquareByGenderEvent()