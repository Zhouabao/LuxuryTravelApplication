package com.sdy.luxurytravelapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.NetworkUtils
import com.cxz.wanandroid.constant.Constants
import com.sdy.luxurytravelapplication.event.NetworkChangeEvent
import com.sdy.luxurytravelapplication.ext.Preference
import org.greenrobot.eventbus.EventBus

/**
 * Created by chenxz on 2018/8/1.
 */
class NetworkChangeReceiver : BroadcastReceiver() {

    /**
     * 缓存上一次的网络状态
     */
    private var hasNetwork: Boolean by Preference(Constants.HAS_NETWORK_KEY, true)

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetworkUtils.isConnected()
        if (isConnected) {
            if (isConnected != hasNetwork) {
                EventBus.getDefault().post(NetworkChangeEvent(isConnected))
            }
        } else {
            EventBus.getDefault().post(NetworkChangeEvent(isConnected))
        }
    }

}