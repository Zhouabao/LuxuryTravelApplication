package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityAboutBinding
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import org.jetbrains.anko.startActivity

/**
 * 关于
 */
class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override fun initData() {

    }

    override fun initView() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.actionbarTitle.text = resources.getString(R.string.about)
            privacyPolicy.setOnClickListener {
                ProtocolActivity.start(this@AboutActivity,ProtocolActivity.TYPE_PRIVACY_PROTOCOL)
            }
            userAgreement.setOnClickListener {
                ProtocolActivity.start(this@AboutActivity,ProtocolActivity.TYPE_USER_PROTOCOL)
            }
            contactUs.setOnClickListener {
                ChatActivity.start(this@AboutActivity, Constants.ASSISTANT_ACCID)
            }

            versionTip.text = "for Android V${getAppVersionName(this@AboutActivity)}"
        }
    }


    //获取当前版本号
    private fun getAppVersionName(context: Context): String {
        var versionName = ""
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = packageInfo.versionName
            if (TextUtils.isEmpty(versionName)) {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return versionName
    }
    override fun start() {
    }

}