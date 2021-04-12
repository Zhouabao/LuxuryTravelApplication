package com.sdy.luxurytravelapplication.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityCountryCodeBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.CountryCodeBean
import com.sdy.luxurytravelapplication.mvp.model.bean.LetterCountryCodeComparator
import com.sdy.luxurytravelapplication.ui.adapter.CountryCodeAdapter
import com.sdy.luxurytravelapplication.utils.GetJsonDataUtil
import com.sdy.luxurytravelapplication.widgets.sortcontacts.Cn2Spell
import com.sdy.luxurytravelapplication.widgets.sortcontacts.PinnedHeaderDecoration
import com.sdy.luxurytravelapplication.widgets.sortcontacts.WaveSideBarView
import org.json.JSONArray
import java.util.*

/**
 * 电话号码区号
 */
class CountryCodeActivity : BaseActivity<ActivityCountryCodeBinding>() {
    override fun initData() {

    }

    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = getString(R.string.choose_country_or_region)
            barCl.btnBack.setOnClickListener { finish() }


            countryCodeRv.layoutManager =
                LinearLayoutManager(this@CountryCodeActivity, RecyclerView.VERTICAL, false)
            val decoration = PinnedHeaderDecoration()
            decoration.registerTypePinnedHeader(1) { _, _ ->
                true
            }
            countryCodeRv.addItemDecoration(decoration)
            countryCodeRv.adapter = adapter
            adapter.setOnItemClickListener { _, view, position ->
                setResult(Activity.RESULT_OK, intent.putExtra("code", adapter.data[position].code))
                finish()
            }


            indexBar.setOnSelectIndexItemListener(object :
                WaveSideBarView.OnSelectIndexItemListener {
                override fun onSelectIndexItem(letter: String) {
                    for (data in adapter.data.withIndex()) {
                        if (data.value.index.equals(letter)) {
                            (countryCodeRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                                data.index,
                                0
                            )
                            return
                        }
                    }
                }

            })
        }
    }

    override fun start() {
        handler.sendEmptyMessage(MSG_LOAD_DATA)
    }


    private val adapter by lazy { CountryCodeAdapter() }

    companion object {
        private const val MSG_LOAD_DATA = 0x0001
        private const val MSG_LOAD_SUCCESS = 0x0002
        private const val MSG_LOAD_FAILED = 0x0003
    }

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
                        Collections.sort(cityBeans, LetterCountryCodeComparator())
                        adapter.addData(cityBeans)
                    }
                    MSG_LOAD_FAILED -> {
                    }
                }
            }
        }
    }


    private val cityBeans: ArrayList<CountryCodeBean> = arrayListOf()
    private fun initJsonData() {
        val data = GetJsonDataUtil().getJson(this, "countrycode.json")
        val datas = parseData(data)
        for (tdata in datas) {

            tdata.index = Cn2Spell.getPinYinFirstLetter(
                if (tdata.sc.isNullOrEmpty()) {
                    "#"
                } else {
                    tdata.sc
                }
            )
        }
        cityBeans.addAll(datas)
        handler.sendEmptyMessage(MSG_LOAD_SUCCESS)
    }


    fun parseData(result: String): ArrayList<CountryCodeBean> {
        val countryCodeBeans = arrayListOf<CountryCodeBean>()
        try {
            val data = JSONArray(result)
            val gson = Gson()
            for (i in 0 until data.length()) {
                val countryCodeBean =
                    gson.fromJson(data.optJSONObject(i).toString(), CountryCodeBean::class.java)
                countryCodeBeans.add(countryCodeBean)
            }
        } catch (e: Exception) {
            handler.sendEmptyMessage(MSG_LOAD_FAILED)
        }

        return countryCodeBeans
    }
}