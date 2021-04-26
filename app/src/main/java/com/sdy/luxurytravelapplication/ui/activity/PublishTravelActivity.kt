package com.sdy.luxurytravelapplication.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.amap.api.services.core.PoiItem
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.util.ClickUtils
import com.google.gson.Gson
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityPublishTravelBinding
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelContract
import com.sdy.luxurytravelapplication.mvp.model.bean.PlanOptionsBean
import com.sdy.luxurytravelapplication.mvp.model.bean.ProviceBean
import com.sdy.luxurytravelapplication.mvp.presenter.PublishTravelPresenter
import com.sdy.luxurytravelapplication.utils.GetJsonDataUtil
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.json.JSONArray
import java.io.Serializable

/**
 * 发布旅行
 */
class PublishTravelActivity :
    BaseMvpActivity<PublishTravelContract.View, PublishTravelContract.Presenter, ActivityPublishTravelBinding>(),
    PublishTravelContract.View,
    View.OnClickListener {
    override fun createPresenter(): PublishTravelContract.Presenter = PublishTravelPresenter()

    override fun useEventBus(): Boolean = true

    companion object {
        private const val MSG_LOAD_DATA = 0x0001
        private const val MSG_LOAD_SUCCESS = 0x0002
        private const val MSG_LOAD_FAILED = 0x0003
        private const val REQUEST_CODE_MAP = 0x0005
        fun start(context: Context) {
            context.startActivity<PublishTravelActivity>()
        }
    }


    override fun initData() {
        binding.apply {
            mLayoutStatusView = binding.stateTravel
            barCl.actionbarTitle.text = "发布旅行"
            barCl.divider.isVisible = true
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    chooseAimBtn,
                    chooseCostBtn,
                    chooseDestinationBtn,
                    chooseRequireBtn,
                    chooseStartBtn,
                    nextBtn
                ), this@PublishTravelActivity
            )
        }

    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.planOptions()
        handler.sendEmptyMessage(MSG_LOAD_DATA)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.chooseAimBtn -> {
                showConditionPicker(binding.aim, "purpose", "请选择旅行目的", planOptionsBean.purpose)
            }
            binding.chooseCostBtn -> {
                showConditionPicker(
                    binding.cost,
                    "cost_type",
                    "请选择费用类型",
                    planOptionsBean.cost_type,
                    planOptionsBean.cost_money,
                    "cost_money"
                )
            }
            binding.chooseDestinationBtn -> {//goal_province[string]	是	目标省 goal_city[string]	是	目标市
                showProvincePicker(
                    binding.destinationPlace,
                    "goal_province",
                    "请选择旅行目的地",
                    provinceBeans,
                    cityBeans,
                    "goal_city"
                )
            }
            binding.chooseRequireBtn -> {
                showConditionPicker(
                    binding.require,
                    "dating_target",
                    "请选择个人要求",
                    planOptionsBean.dating_target
                )
            }
            binding.chooseStartBtn -> {//  rise_province[string]	是	出发省 rise_city[string]	是	出发市
//                params["rise_province"]="四川"
//                params["rise_city"]="成都市福年广场T2"
//                binding.startPlace.text = "${ params["rise_province"]}-${ params["rise_city"]}"
//                checkEnable()
                startActivityForResult<LocationActivity>(REQUEST_CODE_MAP)
            }
            binding.nextBtn -> {
                startActivity<PublishTravelEndActivity>("params" to params as Serializable)
            }
        }
    }


    private lateinit var planOptionsBean: PlanOptionsBean
    override fun planOptions(data: PlanOptionsBean?) {
        if (data == null) {
            mLayoutStatusView?.showError()
        } else {
            mLayoutStatusView?.showContent()
            planOptionsBean = data
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_MAP -> {
                    if (data?.getParcelableExtra<PoiItem>("poiItem") != null) {
                        val positionItem = data.getParcelableExtra<PoiItem>("poiItem")!!
                        params["rise_province"] = positionItem.provinceName
                        params["rise_city"] = positionItem.cityName
                        params["detail_address"] = positionItem.title
                        binding.startPlace.text =
                            "${positionItem.provinceName}-${positionItem.title}"
                        checkEnable()
                    }
                }
            }
        }
    }

    fun checkEnable() {
        binding.nextBtn.isEnabled =
            params["cost_type"] != null && params["cost_money"] != null && params["purpose"] != null
                    && params["rise_province"] != null && params["rise_city"] != null
                    && params["goal_city"] != null && params["dating_target"] != null
                    && params["goal_province"] != null && params["detail_address"] != null
    }


    /**
     * 展示条件选择器
     * content_type[int]	是	1文本 2语音
    content[string]	是	内容
    cost_type[string]	是	花费类型 （男方支出）
    cost_money[string]	是	消费金额
    [string]	是	要求
    purpose[string]	是	目的
    rise_province[string]	是	出发省
    rise_city[string]	是	出发市
    goal_province[string]	是	目标省
    goal_city[string]	是	目标市
     */
    private val params by lazy { hashMapOf<String, Any>() }
    private fun showConditionPicker(
        textview: TextView,
        params1: String,
        title: String,
        optionsItems1: List<String>,
        optionsItems2: List<String> = mutableListOf(),
        params2: String = ""
    ) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                if (optionsItems2.isEmpty()) {
                    textview.text = optionsItems1[options1]
                    params[params1] = optionsItems1[options1]
                } else {
                    textview.text = "${optionsItems1[options1]}·${optionsItems2[options2]}"
                    params[params1] = optionsItems1[options1]
                    params[params2] = optionsItems2[options2]
                }
                checkEnable()
            })
            .setSubmitText(getString(R.string.ok))
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.colorBlack))
            .setTitleSize(16)
            .setDividerColor(resources.getColor(R.color.colorDivider))
            .setContentTextSize(20)
            .setDecorView((window.decorView.findViewById(android.R.id.content)) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .build<String>()

        if (optionsItems2.isNotEmpty()) {
            pvOptions.setNPicker(optionsItems1, optionsItems2, null)
        } else {
            pvOptions.setPicker(optionsItems1)
        }
        pvOptions.show()
    }

    private fun showProvincePicker(
        textview: TextView,
        params1: String,
        title: String,
        optionsItems1: List<ProviceBean>,
        optionsItems2: List<List<ProviceBean>> = mutableListOf(),
        params2: String = ""
    ) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                textview.text =
                    "${optionsItems1[options1].name}-${optionsItems2[options1][options2].name}"
                params[params1] = optionsItems1[options1].name
                params[params2] = optionsItems2[options1][options2].name
                checkEnable()
            })
            .setSubmitText(getString(R.string.ok))
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.colorBlack))
            .setTitleSize(16)
            .setDividerColor(resources.getColor(R.color.colorDivider))
            .setContentTextSize(20)
            .setDecorView((window.decorView.findViewById(android.R.id.content)) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .build<ProviceBean>()
        pvOptions.setPicker(optionsItems1, optionsItems2)
        pvOptions.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


    /*获取省市区数据*/
    private var thread: Thread? = null
    private val handler by lazy {
        @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_LOAD_DATA -> {
                        if (thread == null) {
                            thread = Thread(Runnable {
                                // 子线程中解析省市区数据
                                initJsonData()
                            })
                            thread!!.start()
                        }
                    }
                    MSG_LOAD_SUCCESS -> {
                    }
                    MSG_LOAD_FAILED -> {
                    }
                }
            }
        }
    }

    var provinceBeans = arrayListOf<ProviceBean>()
    val cityBeans = arrayListOf<ArrayList<ProviceBean>>()
    val areaBeans = arrayListOf<ArrayList<ArrayList<ProviceBean>>>()
    private fun initJsonData() {
        val data = GetJsonDataUtil().getJson(this, "region.json")
        val proviceBean = parseData(data)
        provinceBeans = proviceBean

        proviceBean.forEach { parent ->
            val citys = arrayListOf<ProviceBean>()//该省的城市列表（第二级）
            val province_areas = arrayListOf<ArrayList<ProviceBean>>()//该省的所有地区列表（第三极）
            parent.child.forEach { child ->
                citys.add(child)//添加城市
                val city_areas = arrayListOf<ProviceBean>()
                city_areas.addAll(child.child)
                province_areas.add(citys)
            }
            cityBeans.add(citys)
            areaBeans.add(province_areas)
        }



        handler.sendEmptyMessage(MSG_LOAD_SUCCESS)
    }

    private fun parseData(data: String): ArrayList<ProviceBean> {
        val provinces = ArrayList<ProviceBean>()
        try {
            val data = JSONArray(data)
            val gson = Gson()
            for (i in 0 until data.length()) {
                val province =
                    gson.fromJson(data.optJSONObject(i).toString(), ProviceBean::class.java)
                provinces.add(province)
            }
        } catch (e: Exception) {
            handler.sendEmptyMessage(MSG_LOAD_FAILED)
        }
        return provinces
    }


}