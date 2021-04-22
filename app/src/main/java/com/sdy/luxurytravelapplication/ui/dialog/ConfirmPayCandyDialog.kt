package com.sdy.luxurytravelapplication.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.R.string.pay_cancel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.DialogConfirmRechargeCandyBinding
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.event.WxpayResultEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.PayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.PaywayBean
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.sdy.luxurytravelapplication.wxapi.PayResult
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal

class ConfirmPayCandyDialog(
    val myContext: Context,
    val chargeBean: ChargeWayBean,
    val payways: MutableList<PaywayBean>, val source_type: Int = -1
) : BaseBindingDialog<DialogConfirmRechargeCandyBinding>() {
    /**
     * 开始支付
     * payment_type 支付类型 1支付宝 2微信支付 3余额支付
     */
    companion object {
        const val PAY_ALI = 1 //支付宝支付
        const val PAY_WECHAT = 2//微信支付
        const val SDK_PAY_FLAG = 1

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }


    fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = SizeUtils.dp2px(300F)
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation

        window?.attributes = params

        setCanceledOnTouchOutside(false)
    }


    private fun initView() {
        binding.apply {
            price.text = "¥${
            if (!chargeBean.isfirst) {
                if (BigDecimal(chargeBean.discount_price) > BigDecimal.ZERO) {
                    chargeBean.discount_price
                } else {
                    chargeBean.original_price
                }
            } else {
                chargeBean.discount_price
            }}"
            price.typeface = Typeface.createFromAsset(myContext.assets, "DIN_Alternate_Bold.ttf")
            close.setOnClickListener {
//                if (ActivityUtils.getTopActivity() is OpenVipActivity && UserManager.registerFileBean?.experience_state == true) {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                    EventBus.getDefault().post(CloseRegVipEvent(false))
//                }
                dismiss()
            }
            wechatBtn.setOnClickListener {
                wechatCheck.isChecked = true
                alipayCheck.isChecked = false
            }
            alipayBtn.setOnClickListener {
                alipayCheck.isChecked = true
                wechatCheck.isChecked = false
            }


            alipayTv.text = myContext.getString(R.string.pay_alipay)
            wechatTv.text = myContext.getString(R.string.pay_wechat)
            alipayIv.setImageResource(R.drawable.icon_alipay)
            wechatIv.setImageResource(R.drawable.icon_wechat1)

            ClickUtils.applySingleDebouncing(confrimBtn) {
                if (alipayCheck.isChecked) {
                    createOrder(PAY_ALI)
                } else {
                    createOrder(PAY_WECHAT)
                }
            }

            showOtherWayBtn.setOnClickListener {
                wechatCl.isVisible = true
                showOtherWayBtn.isVisible = false
            }
        }
    }


    override fun show() {
        super.show()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    private fun showAlert(ctx: Context, info: String, result: Boolean) {
        MessageDialog.show(
            ActivityUtils.getTopActivity() as AppCompatActivity,
            context.getString(R.string.pay_result),
            info,
            context.getString(
                R.string.confirm
            )
        )
            .setOnOkButtonClickListener { baseDialog, v ->
                if (result) {
                    CommonFunction.payResultNotify(ActivityUtils.getTopActivity())
                    dismiss()
                }
                false
            }
    }



    private fun start2Pay(payment_type: Int, data: PayBean) {
        if (payment_type == PAY_WECHAT) {
            //微信支付
            wechatPay(data)
        } else if (payment_type == PAY_ALI) {
            //支付宝支付
            aliPay(data)
        }
    }

    //pay_id 	    是	支付方式id	展开
    //product_id 	是	购买产品id	展开
    //order_id		是	非必串参数。例如同一商品切换支付方式就需要传
    //payment_type 支付类型 1支付宝 2微信支付 3余额支付
    private fun createOrder(payment_type: Int) {
        val params = hashMapOf<String, Any>()
        for (payway in payways) {
            if (payway.payment_type == payment_type) {
                params["pay_id"] = payway.id
                break
            }
        }
        if (source_type != -1) {
            params["source_type"] = source_type
        }
        params["product_id"] = chargeBean.id
        val loadingDialog =LoadingDialog()
        loadingDialog.show()
        RetrofitHelper.service
            .createOrder(params)
            .ssss {
                loadingDialog.dismiss()
                if (it.code == 200) {
                    //发起微信
                    start2Pay(payment_type, it.data)
                } else {
                    ToastUtil.toast(it.msg)
                }
            }
    }

    /************************   微信支付   ***************************/
    private fun wechatPay(data: PayBean) {
        //微信支付注册
        val wxapi = WXAPIFactory.createWXAPI(myContext, null)
        wxapi.registerApp(Constants.WECHAT_APP_ID)
        if (!wxapi.isWXAppInstalled) {
            ToastUtil.toast(myContext.getString(R.string.unload_wechat))
            return
        }

        //封装微信支付参数
        val request = PayReq()//吊起微信APP的对象
        request.appId = data.wechat?.appid
        request.prepayId = data.wechat?.prepayid
        request.partnerId = data.wechat?.partnerid
        request.nonceStr = data.wechat?.noncestr
        request.timeStamp = data.wechat?.timestamp
        request.packageValue = data.wechat?.`package`
        request.sign = data.wechat?.sign

        //发起微信支付请求
        wxapi.sendReq(request)

    }

    /************************   支付宝支付   ***************************/
    private fun aliPay(data: PayBean) {
        //必须异步调用
        Thread(Runnable {
            val alipay = PayTask(myContext as Activity)
            val result: Map<String, String> = alipay.payV2(data.reqstr, true)
            Log.i("msp", result.toString())

            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }).start()
    }



    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    run {
                        val payResult = PayResult(msg.obj as Map<String, String>)

                        /**
                         * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        val resultInfo = payResult.result// 同步返回需要验证的信息
                        val resultStatus = payResult.resultStatus
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            showAlert(myContext, myContext.getString(R.string.pay_success), true)
                        } else if (TextUtils.equals(resultStatus, "8000")) {
                            showAlert(
                                myContext,
                                myContext.getString(R.string.pay_checking),
                                false
                            )
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            showAlert(
                                myContext,
                                myContext.getString(pay_cancel),
                                false
                            )

                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            showAlert(myContext, myContext.getString(R.string.pay_fail), false)
                        }

                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseDialogEvent(event: CloseDialogEvent) {
        if (isShowing) {
            dismiss()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWxpayResultEvent(event: WxpayResultEvent) {
        when (event.code) {
            com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_OK -> {
                showAlert(myContext, myContext.getString(R.string.pay_success), true)
            }
            com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_USER_CANCEL -> {
                showAlert(myContext, myContext.getString(pay_cancel), false)
            }
            com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_COMM -> {
                showAlert(myContext, myContext.getString(pay_cancel), false)
            }
            else -> {
                showAlert(myContext, myContext.getString(R.string.pay_error), false)
            }
        }
    }


}
