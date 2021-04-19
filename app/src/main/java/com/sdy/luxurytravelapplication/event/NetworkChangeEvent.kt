package com.sdy.luxurytravelapplication.event

import android.content.Context
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.mvp.model.bean.Alipay
import com.sdy.luxurytravelapplication.mvp.model.bean.GiftBean
import java.io.File

/**
 * Created by chenxz on 2018/8/1.
 */
class NetworkChangeEvent(var isConnected: Boolean)

class CloseRegVipEvent(val paySuccess: Boolean)

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


/*语音播放单例回调*/
class OneVoicePlayEvent(val playPosition: Int, val type: Int, val context: Context)

/**
 * 录制完成
 */
class RecordCompleteEvent(val duration: Int, val filePath: String)


/**
 * 动态发布上传
 */
class UploadEvent(
    var totalFileCount: Int = 0,
    var currentFileIndex: Int = 0,
    var progress: Double = 0.0,
    var qnSuccess: Boolean = true,
    var from: Int = FROM_SQUARE
) {
    companion object {
        val FROM_SQUARE = 1
        val FROM_USERCENTER = 2
    }
}

//上传成功或者失败事件
/**
 * @param serverSuccess 成功或者失败
 * @param  code失败的code码 判断是否是审核不通过
 */
class AnnounceEvent(var serverSuccess: Boolean = false, var code: Int = 0)


//重新上传内容的通知成功或者失败事件
class RePublishEvent(var republish: Boolean, val context: String)



//刷新加入甜心圈显示
class RefreshSweetAddEvent(val isHoney: Boolean = false)


//首页更新红点消息
class GetNewMsgEvent
/**
 * 更新招呼列表
 */
class UpdateHiEvent

class UpdateAccostListEvent()



/**
 * 点击选中赠送礼物事件
 */
class UpdateChatCallGiftEvent(
    val giftbean: GiftBean,
    val parentPosition: Int,
    val childPosition: Int
)

class CloseDialogEvent


class WxpayResultEvent(val code: Int)


/**
 * 获取支付宝账号事件总线
 */
class GetAlipayAccountEvent(val account: Alipay)

/**
 * 更新设置
 */
class UpdateSettingEvent


//更新用户中心信息
class UserCenterEvent(var refresh: Boolean)


//账号异常认证事件通知
class AccountDangerEvent(val type: Int)




//更新用户联系方式
class UserCenterContactEvent(var contact_way: Int)



class UpdateApproveEvent()

//女性是否录制视频
class FemaleVideoEvent(val videoState: Int)

//置顶卡片
class TopCardEvent(val showTop: Boolean)
//刷新甜心圈认证状态
class RefreshSweetEvent()



/**
 * 更新发送礼物的事件
 */
class UpdateSendGiftEvent(val message: IMMessage)

class HideChatLlEvent()

class HideContactLlEvent()

class RefreshCandyMessageEvent(val orderId: Int, val state: Int)