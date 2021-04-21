package com.sdy.luxurytravelapplication.ui.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.ui.activity.MyInfoActivity
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/4/2119:35
 *    desc   :
 *    version: 1.0
 */
class GotoVerifyDialog : Dialog {
    companion object {
        const val TYPE_VERIFY = 4//认证失败去认证
        const val TYPE_CHANGE_AVATOR_NOT_PASS = 7//头像违规替换
        const val TYPE_CHANGE_AVATOR_REAL_NOT_VALID = 11//真人头像不合规
    }

    //  var  ImageView ivDialogCancel? = null;
    var tvTitle: TextView? = null
    var ivDialogIcon: ImageView? = null
    var tvDialogContent: TextView? = null
    var btDialogConfirm: TextView? = null //确定按钮可通过外部自定义按钮内容
    var tvDialogCancel: TextView? = null //取消
    var viewDialog: View? = null //分割线
    var llVerify: LinearLayout? = null //认证的布局
    var verifyChange: TextView? = null //换头像
    var verifyHuman: TextView? = null //人工审核
    var verifyCancel: TextView? = null //取消
    var iconWarn: ImageView? = null //警告的图标
    var llConfirmOrCancel: LinearLayout? = null //确认或者取消


    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, themeStyle: Int) : super(context, themeStyle) {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.dialog_goto_verify_layout)
        initWindow()
        //        ivDialogCancel = findViewById(R.id.iv_dialog_cancel);
        tvTitle = findViewById(R.id.title)
        ivDialogIcon = findViewById(R.id.icon)
        tvDialogContent = findViewById(R.id.message)
        btDialogConfirm = findViewById(R.id.confirm)
        tvDialogCancel = findViewById(R.id.cancel)
        viewDialog = findViewById(R.id.cancelView)
        llVerify = findViewById(R.id.llVerify)
        verifyHuman = findViewById(R.id.gotoHumanVerify)
        verifyChange = findViewById(R.id.changeAvator)
        verifyCancel = findViewById(R.id.cancelVerify)
        llConfirmOrCancel = findViewById(R.id.llConfirmOrCancel)
        iconWarn = findViewById(R.id.iconWarn)
    }


    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        // 设置窗口背景透明度
//        params?.alpha = 0.5f
//         android:layout_marginLeft="15dp"
//        android:layout_marginRight="15dp"
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = params

    }

    class Builder(val context: Context) {
        var title: String? = null
        var icon: Any? = 0
        var iconVisble: Boolean = false
        var content: String? = null
        var btConfirmText: String? = null
        var tvCancelText: String? = null
        var cancelIsVisibility: Boolean? = true
        var cancelable: Boolean = true
        var type: Int = 4//弹框的类型


        fun setOnCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setType(type: Int): Builder {
            this.type = type
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setIconVisible(visible: Boolean): Builder {
            this.iconVisble = visible
            return this
        }

        fun setIcon(icon: Any): Builder {
            this.icon = icon
            return this
        }

        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        // 点击确定按钮的文字
        fun setConfirmText(btConfirmText: String): Builder {
            this.btConfirmText = btConfirmText
            return this
        }

        //取消按钮的文字
        fun setCancelText(tvCancelText: String): Builder {
            this.tvCancelText = tvCancelText
            return this
        }

        fun setCancelIconIsVisibility(cancelIsVisibility: Boolean): Builder {
            this.cancelIsVisibility = cancelIsVisibility
            return this
        }

        fun create(): GotoVerifyDialog {
            val dialog = GotoVerifyDialog(context, R.style.MyDialog)
            if (!TextUtils.isEmpty(title)) {
                dialog.tvTitle?.text = this.title
            } else {
                dialog.tvTitle?.visibility = View.GONE
            }
            dialog.tvDialogContent?.text = this.content
            if (icon != 0) {
                GlideUtil.loadImg(context, this.icon!!, dialog.ivDialogIcon!!)
            }
            dialog.ivDialogIcon?.isVisible = this.iconVisble
            //点击外部可取消
            dialog.setCanceledOnTouchOutside(this.cancelable)
            dialog.setOnKeyListener { dialogInterface, keyCode, event ->
                if (this.cancelable) {
                    false
                } else
                    keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0
            }
            dialog.iconWarn?.isVisible =
                (this.type == TYPE_VERIFY || this.type == TYPE_CHANGE_AVATOR_NOT_PASS)


            if (this.type == TYPE_VERIFY) {
                dialog.llVerify?.isVisible = true
                dialog.llConfirmOrCancel?.isVisible = false
                dialog.verifyCancel?.setOnClickListener {
                    humanVerify(2)
                    dialog.cancel()
                }
                dialog.verifyHuman?.setOnClickListener {
                    // 人工审核
                    humanVerify(1)
                    if (this.cancelable)
                        dialog.cancel()
                }
                dialog.verifyChange?.setOnClickListener {
                    // 替换头像
                    humanVerify(2)
                    if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                        context.startActivity<MyInfoActivity>()
                    if (this.cancelable)
                        dialog.cancel()
                }
            } else {
                dialog.llVerify?.isVisible = false
                dialog.llConfirmOrCancel?.isVisible = true

                dialog.btDialogConfirm?.text = this.btConfirmText ?: context.getString(R.string.ok)
                if (this.cancelIsVisibility!!) {
                    dialog.tvDialogCancel?.text =
                        this.tvCancelText ?: context.getString(R.string.cancel)
                } else {
                    dialog.tvDialogCancel?.visibility = View.GONE
                    dialog.viewDialog?.visibility = View.GONE
                }
                dialog.tvDialogCancel?.setOnClickListener {
                    if (this.cancelable)
                        dialog.cancel()
                }
                dialog.btDialogConfirm?.setOnClickListener {
                    if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                        context.startActivity<MyInfoActivity>()
                    dialog.cancel()
                }
            }


            return dialog
        }


        /**
         * 人工审核
         * 1 人工认证 2重传头像或则取消
         */
        fun humanVerify(type: Int) {
            val params = hashMapOf<String, Any>("type" to type)
            RetrofitHelper.service
                .humanAduit(params)
        }


    }

}