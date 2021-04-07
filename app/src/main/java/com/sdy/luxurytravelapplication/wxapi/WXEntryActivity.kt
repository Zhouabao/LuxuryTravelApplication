package com.sdy.luxurytravelapplication.wxapi

import android.os.Bundle
import com.sdy.luxurytravelapplication.R
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.umeng.socialize.weixin.view.WXCallbackActivity

/**
 * 微信登录回调
 */
class WXEntryActivity : WXCallbackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_w_x_entry)
    }


    override fun onResp(resp: BaseResp) {
        super.onResp(resp)//一定要加super，实现我们的方法，否则不能回调
    }


}