package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogLoginOffBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.ReportBean
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginOffCauseBeans
import com.sdy.luxurytravelapplication.ui.activity.VerifycodeActivity
import com.sdy.luxurytravelapplication.ui.adapter.ReportResonAdapter
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.sdy.luxurytravelapplication.widgets.DividerItemDecoration
import org.jetbrains.anko.startActivity

class LoginOffDialog(
    val phone: String?,
    val LoginOffCauseBeans: LoginOffCauseBeans
) : BaseBindingDialog<DialogLoginOffBinding>() {


    private var checkReason = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
        setData()
    }

    private val adapter by lazy { ReportResonAdapter() }
    private fun initView() {
        binding.apply {
            confirmLoginoffBtn.setOnClickListener {
                context.startActivity<VerifycodeActivity>(
                    "type" to VerifycodeActivity.TYPE_LOGIN_OFF,
                    "descr" to checkReason,
                    "phone" to phone
                )
                dismiss()
            }

            loginOffReasonRv.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            loginOffReasonRv.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.HORIZONTAL_LIST,
                    SizeUtils.dp2px(1f),
                    context.resources.getColor(R.color.colorDivider)
                )
            )
            loginOffReasonRv.adapter = adapter
            adapter.addChildClickViewIds(R.id.reportReason)
            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.reportReason -> {
                        for (data in adapter.data.withIndex()) {
                            if (data.index == position)
                                checkReason = data.value.reason
                            data.value.checked = data.index == position
                        }
                        adapter.notifyDataSetChanged()
                        checkConfirmEnable()
                    }
                }
            }
        }
    }

    private fun setData() {
        binding.loginOffWarning.text = LoginOffCauseBeans.descr
        for (cause in LoginOffCauseBeans.list.withIndex()) {
            if (cause.index == 0)
                checkReason = cause.value
            adapter.addData(ReportBean(cause.value, cause.index == 0))
        }
        checkConfirmEnable()
    }

    private fun checkConfirmEnable() {
        for (data in adapter.data) {
            if (data.checked) {
                binding.confirmLoginoffBtn.isEnabled = true
                break
            } else {
                binding.confirmLoginoffBtn.isEnabled = false
            }
        }
    }


    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
//        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
//        params?.y = SizeUtils.dp2px(20F)
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }
}
