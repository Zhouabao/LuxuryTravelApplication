package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.StringUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityNickNameBinding

/**
 * 修改昵稱和簽名
 * //1昵称 2签名
 */
class NickNameActivity : BaseActivity<ActivityNickNameBinding>() {
    override fun initData() {
    }

    val type by lazy { intent.getIntExtra("type", 0) }

    override fun initView() {
        binding.apply {
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            if (type == 1) {
                barCl.actionbarTitle.text = getString(R.string.nickname_title)
                changeEt.hint = getString(R.string.nickname_change_tip)
                changeEt.filters = arrayOf(InputFilter.LengthFilter(10))
                val params = changeEt.layoutParams as ConstraintLayout.LayoutParams
                params.width = LinearLayout.LayoutParams.MATCH_PARENT
                params.height = SizeUtils.dp2px(50F)
                changeEt.layoutParams = params
                changeEt.setText(intent.getStringExtra("content"))
                changeEt.setSelection(changeEt.text.length)
            } else {
                changeEtInputLength.isVisible = true
                barCl.actionbarTitle.text = getString(R.string.about_me_title)
                changeEt.hint = getString(R.string.about_me_tip)
                changeEt.filters = arrayOf(InputFilter.LengthFilter(200))
                val params = changeEt.layoutParams as ConstraintLayout.LayoutParams
                params.width = LinearLayout.LayoutParams.MATCH_PARENT
                params.height = SizeUtils.dp2px(254F)
                changeEt.layoutParams = params
                if (!(intent.getStringExtra("content").isNullOrBlank())) {
                    changeEt.setText(intent.getStringExtra("content"))
                    changeEt.setSelection(changeEt.text.length)
                }
            }
            changeEtInputLength.text = SpanUtils.with(changeEtInputLength)
                .append(changeEt.length().toString())
                .setFontSize(14, true)
                .setForegroundColor(resources.getColor(R.color.colorAccent))
                .append("/${(changeEt.filters[0] as InputFilter.LengthFilter).max}")
                .setFontSize(10, true)
                .create()


            changeEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable) {
                    barCl.rightTextBtn.isEnabled = !StringUtils.isTrimEmpty(p0.toString())
                    changeEtInputLength.text = SpanUtils.with(changeEtInputLength)
                        .append("${p0.trim().length}")
                        .setFontSize(14, true)
                        .setForegroundColor(resources.getColor(R.color.colorAccent))
                        .setBold()
                        .append("/${(changeEt.filters[0] as InputFilter.LengthFilter).max}")
                        .setFontSize(10, true)
                        .create()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })
            barCl.btnBack.setOnClickListener {
                onBackPressed()
            }
            barCl.rightTextBtn.setOnClickListener {
                intent.putExtra("content", changeEt.text.toString())
                setResult(Activity.RESULT_OK, intent)
                onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            changeEt.postDelayed({ KeyboardUtils.showSoftInput(changeEt) }, 100)
        }
    }
    override fun onBackPressed() {
        KeyboardUtils.hideSoftInput(binding.changeEt)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.hideSoftInput(binding.changeEt)
    }

    override fun finish() {
        super.finish()
        KeyboardUtils.hideSoftInput(binding.changeEt)
    }

    override fun start() {


    }

}