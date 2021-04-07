package com.sdy.luxurytravelapplication.wxapi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.event.WxpayResultEvent
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus

class WXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private val wxApi by lazy { WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wxApi.handleIntent(intent, this)

    }

    override fun onNewIntent(paramIntent: Intent?) {
        super.onNewIntent(paramIntent)
        intent = paramIntent
        wxApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp) {
        LogUtils.d(resp)
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            finish()
            overridePendingTransition(0, 0)
            EventBus.getDefault().post(WxpayResultEvent(resp.errCode))
        }
//        else if (resp.type == ConstantsAPI.COMMAND_SENDAUTH) { }
    }

    override fun onReq(p0: BaseReq?) {

    }


}
