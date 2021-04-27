package com.sdy.luxurytravelapplication.nim.impl.provider

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider
import com.sdy.luxurytravelapplication.nim.location.NavigationAmapActivity
import com.sdy.luxurytravelapplication.ui.activity.LocationActivity

/**
 * Created by zhoujianghua on 2015/8/11.
 */
class NimDemoLocationProvider : LocationProvider {
    override fun requestLocation(
        context: Context,
        callback: LocationProvider.Callback
    ) {

            PermissionUtils.permission(PermissionConstants.LOCATION)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        LocationActivity.start(context, callback)
//                        LocationAmapActivity.start(context, callback)
                    }

                    override fun onDenied() {
                        MessageDialog.show(
                            (context as AppCompatActivity),
                            context.getString(R.string.location_is_not_available),
                            context.getString(
                                R.string.please_open_location
                            ),
                            context.getString(R.string.ok),
                            context.getString(R.string.cancel)
                        )
                            .setOnCancelButtonClickListener { _: BaseDialog, _: View ->
                                false
                            }
                            .setOnOkButtonClickListener { _: BaseDialog, _: View ->
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    LogUtils.e("LOC", "start ACTION_LOCATION_SOURCE_SETTINGS error")
                                }
                                false
                            }
                    }
                })
                .request()

    }

    override fun openMap(
        context: Context,
        longitude: Double,
        latitude: Double,
        address: String
    ) {
        val intent = Intent(context, NavigationAmapActivity::class.java)
        intent.putExtra(LocationActivity.LONGTITUDE, longitude)
        intent.putExtra(LocationActivity.LATITUDE, latitude)
        intent.putExtra(LocationActivity.ADDRESS, address)
        context.startActivity(intent)


    }
}