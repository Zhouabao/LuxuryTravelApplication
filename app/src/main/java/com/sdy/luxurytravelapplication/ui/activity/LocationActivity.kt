package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityLocationBinding
import com.sdy.luxurytravelapplication.ui.adapter.LocationAdapter
import com.sdy.luxurytravelapplication.widgets.DividerItemDecoration
import kotlin.math.sqrt

/**
 * 选择定位
 */
class LocationActivity : BaseActivity<ActivityLocationBinding>(), PoiSearch.OnPoiSearchListener,
    View.OnClickListener,
    AMapLocationListener {
    private var mLocationClient: AMapLocationClient? = null

    //创建AMapLocationClientOption对象
    private val mLocationOption by lazy { AMapLocationClientOption() }
    private var location: AMapLocation? = null

    //关键字搜素
    private var mQuery: PoiSearch.Query? = null
    private var mPoiSearch: PoiSearch? = null

    private val adapter by lazy { LocationAdapter() }

    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.scrollLocationSv) }
    private var expand = false
    private lateinit var aMap: AMap
    override fun initData() {
        initMap(savedInstanceState = null)
        initLocation()
    }

    override fun initView() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.actionbarTitle.text = getString(R.string.choose_location)
            barCl.rightTextBtn.text = getString(R.string.ok)
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            barCl.rightTextBtn.setOnClickListener(this@LocationActivity)
            backToMyLocationBtn.setOnClickListener(this@LocationActivity)

            bottomSheetBehavior.peekHeight = SizeUtils.dp2px(263F)
            bottomSheetBehavior.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                Log.d("slideOffset", "slideOffset=====${slideOffset}")
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        KeyboardUtils.hideSoftInput(this@LocationActivity)
                        expandBtn.setImageResource(R.drawable.icon_search_expand)
                    } else {
                        expandBtn.setImageResource(R.drawable.icon_search_collapsed)
                    }
//                val params = bottomSheet.layoutParams
//                if (params.height > SizeUtils.dp2px(432F)) {
//                    params.height = SizeUtils.dp2px(432F)
//                    bottomSheet.layoutParams = params
//                }
//                Log.d("slideOffset", "slideOffset=====${params.height}")
                }

            })



            locationRv.layoutManager =
                LinearLayoutManager(this@LocationActivity, RecyclerView.VERTICAL, false)
            locationRv.addItemDecoration(
                DividerItemDecoration(
                    this@LocationActivity, DividerItemDecoration.HORIZONTAL_LIST,
                    SizeUtils.dp2px(1F),
                    resources.getColor(R.color.colorDivider)
                )
            )
            locationRv.adapter = adapter
            adapter.setOnItemClickListener { _, view, position ->
//            if (adapter.checkPosition != position) {
                adapter.checkPosition = position
                adapter.notifyDataSetChanged()
                if (position != 0) {
                    isTouch = false
                    moveMapCamera(
                        adapter.data[position].latLonPoint.latitude,
                        adapter.data[position].latLonPoint.longitude
                    )
                }
                searchLocation.clearFocus()
//            }
            }


            searchLocation.setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    KeyboardUtils.hideSoftInput(this@LocationActivity)
                }
            }


            searchLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    keyWordSearch(query ?: "")
                    searchLocation.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    keyWordSearch(newText ?: "")
                    return true
                }

            })

        }
    }

    override fun start() {
    }

    private var isTouch = false
    private fun initMap(savedInstanceState: Bundle?) {
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        binding.locationMap.onCreate(savedInstanceState)
        //初始化地图控制器对象
        aMap = binding.locationMap.map
//        aMap.setCustomMapStyle(
//            CustomMapStyleOptions()
//                .setEnable(true)
//                .setStyleData(getAssetsStyle("style.data"))
//                .setStyleExtraData(getAssetsStyle("style_extra.data"))
//        )
//        aMap.setWorldVectorMapStyle("style_local")
//        if (!UserManager.overseas) {
//            aMap.accelerateNetworkInChinese(true)
//        }
        aMap.mapType = AMap.MAP_TYPE_NORMAL
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom))
        aMap.uiSettings.isZoomControlsEnabled = false
        // 对amap添加移动地图事件监听器
        aMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChangeFinish(p0: CameraPosition) {
                if (isTouch)
                    doWhenLocationSuccess(p0.target.latitude, p0.target.longitude)
                screenMoveMarker!!.position = LatLng(p0.target.latitude, p0.target.longitude)
                startJumpAnimation()
            }

            override fun onCameraChange(p0: CameraPosition) {

            }

        })

        aMap.setOnMapClickListener {
            doWhenLocationSuccess(it.latitude, it.longitude)
            screenMoveMarker!!.position = it
            startJumpAnimation()
        }

        // 对amap添加触摸地图事件监听器
        aMap.setOnMapTouchListener {
            isTouch = true
        }

    }

    private fun initLocation() {
        if (mLocationClient == null) {
            mLocationClient = AMapLocationClient(this)
            //获取定位结果
            mLocationClient!!.setLocationListener(this)
            mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationClient!!.setLocationOption(mLocationOption)
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            mLocationClient!!.stopLocation()
            mLocationClient!!.startLocation()
        }

    }


    private val zoom = 19f//地图缩放级别
    private fun moveMapCamera(latitude: Double, longitude: Double) {
        if (aMap != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        }

        startJumpAnimation()
    }


    private val poiCode by lazy { getString(R.string.poi_name) }

    private fun doWhenLocationSuccess(latitude: Double, longitude: Double) {
        //120200楼宇 190107街道
//        地名地址信息|道路附属设施|公共设施  地名地址信息|道路附属设施|公共设施|商务住宅
        mQuery = PoiSearch.Query("", poiCode, "")

        mQuery!!.pageSize = 100
        mQuery!!.pageNum = 0//设置查询第一页
        mPoiSearch = PoiSearch(this, mQuery)
        mPoiSearch!!.setOnPoiSearchListener(this)
        mPoiSearch!!.bound =
            PoiSearch.SearchBound(LatLonPoint(latitude, longitude), 10 * 1000, true)
        mPoiSearch!!.searchPOIAsyn()// 异步搜索

    }

    private fun keyWordSearch(keyword: String) {
        if (TextUtils.isEmpty(keyword)) {
//            mLocationClient?.stopLocation()
            mLocationClient?.startLocation()
            return
        }

        //120200楼宇 190107街道
//        地名地址信息|道路附属设施|公共设施
        mQuery = PoiSearch.Query(keyword, poiCode, UserManager.city)
        mQuery!!.pageSize = 100
        mQuery!!.pageNum = 0//设置查询第一页
        mPoiSearch = PoiSearch(this, mQuery)
        mPoiSearch!!.setOnPoiSearchListener(this)
        mPoiSearch!!.searchPOIAsyn()// 异步搜索
    }

    private var screenMoveMarker: Marker? = null
    private var growMarker: Marker? = null

    private fun addScreenMoveMarker() {
        if (screenMoveMarker == null) {
            val latLng = aMap.cameraPosition.target
            val screenPoint = aMap.projection.toScreenLocation(latLng)
            screenMoveMarker = aMap.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_anchor))
                    .position(LatLng(location!!.latitude, location!!.longitude))
                    .anchor(0.5f, 0.5f)
            )
            screenMoveMarker!!.setPositionByPixels(screenPoint.x, screenPoint.y)
        }
    }

    fun startJumpAnimation() {
        if (screenMoveMarker == null) {
            addScreenMoveMarker()
        } else
            if (screenMoveMarker != null) {
                val latLng = screenMoveMarker!!.position
                val point = aMap.projection.toScreenLocation(latLng)
                point.y = point.y - SizeUtils.dp2px(125F)

                val target = aMap.projection.fromScreenLocation(point)
                val animation = TranslateAnimation(target)
                animation.setInterpolator { it ->
                    (if (it <= 0.5f) {
                        0.5F - 2 * (0.5f - it) * (0.5f - it)
                    } else {
                        0.5F - sqrt((it - 0.5F) * (1.5f - it))
                    })
                }
                animation.setDuration(600)
                screenMoveMarker!!.setAnimation(animation)
                screenMoveMarker!!.startAnimation()
            }

    }


    override fun onPause() {
        super.onPause()
        binding.locationMap.onPause()
        if (null != mLocationClient) {
            mLocationClient!!.stopLocation()
            mLocationClient!!.onDestroy();
            mLocationClient = null

        }
    }

    override fun onResume() {
        super.onResume()
        binding.locationMap.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.locationMap.onDestroy()
        if (null != mLocationClient) {
            mLocationClient!!.stopLocation()
            mLocationClient!!.onDestroy()
            mLocationClient = null
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
    }

    override fun onPoiSearched(result: PoiResult?, rCode: Int) {
        if (rCode == 1000) {
            if (result != null && result.query != null) {
                adapter.setNewData(result.pois)
                binding.barCl.rightTextBtn.isEnabled = true

                if (adapter.data.size > 1) {
                    adapter.checkPosition = 1
                } else {
                    adapter.checkPosition = 0
                }
                adapter.notifyDataSetChanged()
            }
        }
    }


    override fun onLocationChanged(it: AMapLocation?) {
        if (it != null) {
            if (mLocationClient != null) mLocationClient!!.stopLocation()
            if (it.errorCode == 0) {
                location = it
                //可在其中解析amapLocation获取相应内容。
                UserManager.saveLocation(
                    "${it.latitude}",
                    "${it.longitude}",
                    it.province,
                    it.city
                )
                addScreenMoveMarker()
                doWhenLocationSuccess(location!!.latitude, location!!.longitude)
                moveMapCamera(location!!.latitude, location!!.longitude)
            } else {
                LogUtils.e("${it.errorCode},,,,${it.errorInfo}")
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.backToMyLocationBtn-> {
                mLocationClient?.stopLocation()
                mLocationClient?.startLocation()
            }
            binding.barCl.rightTextBtn-> {
                if (adapter.data.size > adapter.checkPosition) {
                    setResult(
                        Activity.RESULT_OK,
                        intent.putExtra("poiItem", adapter.data[adapter.checkPosition])
                    )
                } else {
                    setResult(Activity.RESULT_OK)
                }
                finish()
            }
        }

    }


}