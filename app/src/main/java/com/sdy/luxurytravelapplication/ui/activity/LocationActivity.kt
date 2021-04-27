package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityLocationBinding
import com.sdy.luxurytravelapplication.databinding.LayoutLocationMyLocationBinding
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider
import com.sdy.luxurytravelapplication.ui.adapter.LocationAdapter
import com.sdy.luxurytravelapplication.widgets.DividerItemDecoration
import org.jetbrains.anko.startActivity
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 选择定位
 */
class LocationActivity : BaseActivity<ActivityLocationBinding>(), PoiSearch.OnPoiSearchListener,
    View.OnClickListener,
    AMapLocationListener, AMap.OnMapScreenShotListener, LocationSource,
    AMap.OnMyLocationChangeListener {
    private val locationMessage: IMMessage? by lazy { intent.getSerializableExtra(MESSAGE) as IMMessage? }
    private val longitude by lazy { locationMessage?.remoteExtension?.get(EXTENSION_LONGITUDE) as Double }
    private val latitude by lazy { locationMessage?.remoteExtension?.get(EXTENSION_LATITUDE) as Double }

    companion object {
        const val EXTENSION_NAME = "name"
        const val EXTENSION_ADDRESS = "address"
        const val EXTENSION_LATITUDE = "latitude"
        const val EXTENSION_LONGITUDE = "longitude"

        const val MESSAGE = "message"
        const val NICKNAME = "nickname"
        const val LATITUDE = "latitude"
        const val LONGTITUDE = "longtitude"
        const val ADDRESS = "address"
        var callback: LocationProvider.Callback? = null

        @JvmOverloads
        fun start(context: Context, callback: LocationProvider.Callback? = null) {
            this.callback = callback
            context.startActivity<LocationActivity>()
        }

        @JvmOverloads
        fun startedLocated(context: Context, imMessage: IMMessage? = null) {
            context.startActivity<LocationActivity>(MESSAGE to imMessage)
        }
    }


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
            if (locationMessage == null) {
                barCl.actionbarTitle.text = getString(R.string.choose_location)
                barCl.rightTextBtn.text = getString(R.string.ok)
                barCl.rightTextBtn.setTextColor(Color.WHITE)
                barCl.rightTextBtn.isVisible = true
                barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
                barCl.rightTextBtn.setOnClickListener(this@LocationActivity)
                backToMyLocationBtn.setOnClickListener(this@LocationActivity)
                backToMyLocationBtn.isVisible = true
                scrollLocationSv.isVisible = true
                locationUserInfoCl.isVisible = false
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

            } else {
                barCl.actionbarTitle.text =
                    getString(R.string.someone_position, locationMessage!!.fromNick)
                backToMyLocationBtn.isVisible = false
                scrollLocationSv.isVisible = false
                (locationMap.layoutParams as ConstraintLayout.LayoutParams).bottomMargin = 0

                locationUserInfoCl.isVisible = true
                userAvator.loadBuddyAvatar(locationMessage!!)
                userLocationName.text = locationMessage!!.remoteExtension[EXTENSION_NAME].toString()
                userLocationDescr.text =
                    locationMessage!!.remoteExtension[EXTENSION_ADDRESS].toString()

            }

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
        aMap.mapType = AMap.MAP_TYPE_NORMAL
        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom))
        aMap.uiSettings.isZoomControlsEnabled = false
        aMap.setLocationSource(this)//设置定位数据源的监听

        initMyStyle()
        //点击回到当前定位位置
        aMap.setOnMyLocationChangeListener(this)

        if (locationMessage == null) {
            // 对amap添加移动地图事件监听器
            aMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
                override fun onCameraChangeFinish(p0: CameraPosition) {
                    if (isTouch)
                        doWhenLocationSuccess(p0.target.latitude, p0.target.longitude)
                    addScreenMoveMarker(p0.target.latitude, p0.target.longitude)
                }

                override fun onCameraChange(p0: CameraPosition) {

                }

            })

            aMap.setOnMapClickListener {
                doWhenLocationSuccess(it.latitude, it.longitude)
                addScreenMoveMarker(it.latitude, it.longitude)
            }

            // 对amap添加触摸地图事件监听器
            aMap.setOnMapTouchListener {
                isTouch = true
            }
        } else {
            aMap.uiSettings.isZoomGesturesEnabled = true
        }

    }

    private fun initMyStyle() {
        val style = MyLocationStyle()
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        //设置定位蓝点
        style.myLocationIcon(BitmapDescriptorFactory.fromBitmap(createMyLocationView()))
        style.strokeColor(Color.TRANSPARENT)
        style.radiusFillColor(Color.TRANSPARENT)
        style.showMyLocation(true)
        aMap.myLocationStyle = style
        aMap.isMyLocationEnabled = true //可触发定位并显示当前位置
    }


    /**
     * 我的定位蓝点
     */
    private fun createMyLocationView(): Bitmap? {
        var bitmap: Bitmap? = null
        Glide.with(this)
            .asBitmap()
            .load(UserManager.avatar)
            .thumbnail(0.2f)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val binding = LayoutLocationMyLocationBinding.inflate(layoutInflater)
                    binding.userAvatorLocation.setImageBitmap(resource)
                    bitmap = convertViewToBitmap(binding.root)
                }
            })
        return bitmap
    }

    /**
     * bitmap转换
     */
    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        view.buildDrawingCache()

        return view.drawingCache

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
    private fun addScreenMoveMarker(latitude: Double, longitude: Double) {
        if (screenMoveMarker != null) {
            screenMoveMarker!!.remove()
            screenMoveMarker = null
        }
        screenMoveMarker = aMap.addMarker(
            MarkerOptions()
                .icon(
                    BitmapDescriptorFactory.fromView(
                        LayoutInflater.from(this)
                            .inflate(R.layout.layout_location_move_located, null)
                    )
                )
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.75f)
        )
        moveMapCamera(latitude, longitude)
    }


    override fun onPause() {
        super.onPause()
        binding.locationMap.onPause()
        stopLocate()
    }

    override fun onResume() {
        super.onResume()
        binding.locationMap.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.locationMap.onDestroy()
        stopLocate()
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

    private fun addMyScreeMarker(latitude: Double, longitude: Double) {
        aMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(createMyLocationView()))
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.75f)
        )
//        BitmapDescriptorFactory.fromBitmap(createMyLocationView())
    }

    override fun onLocationChanged(it: AMapLocation?) {
        if (locationMessage != null) {
            if (it != null) {
                if (mLocationClient != null) mLocationClient!!.stopLocation()
                if (it.errorCode == 0) {
                    addScreenMoveMarker(latitude, longitude)
                    addMyScreeMarker(it.latitude, it.longitude)
                } else {
                    mLocationClient?.startLocation()
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    LogUtils.d("locationMessage ${it.errorCode},${it.errorInfo}")
                }
            }
        } else {
            if (it != null) {
                LogUtils.d("onLocationChanged=== ${it.errorCode},${it.errorInfo}")
                if (mLocationClient != null) mLocationClient!!.stopLocation()
                if (it.errorCode == 0) {
                    if (mListener != null) {
                        mListener!!.onLocationChanged(it)
                    }
                    //可在其中解析amapLocation获取相应内容。
                    addScreenMoveMarker(it.latitude, it.longitude)
                    doWhenLocationSuccess(it.latitude, it.longitude)
                    // moveMapCamera(location!!.latitude, location!!.longitude)
                } else {
                    mLocationClient?.startLocation()
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    LogUtils.d("${it.errorCode},${it.errorInfo}")
                }
            }
        }
    }

    /**
     * 地图截图
     */
    private fun screenShot() {
        aMap.myLocationStyle.showMyLocation(false)
//        aMap.clear()

        aMap.getMapScreenShot(this)


    }

    override fun onMapScreenShot(bitmap: Bitmap?) {
        if (bitmap == null || checkedItem == null) {
            return
        }
        val path = "${getExternalFilesDir(null)}/map_shoot_${System.currentTimeMillis()}.png"
        try {
            val fos = FileOutputStream(path)
            val b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            try {
                fos.flush()
            } catch (e: IOException) {
                Log.e("screenShot", e.printStackTrace().toString())
            }
            try {
                fos.close()
            } catch (e: IOException) {
                Log.e("screenShot", e.printStackTrace().toString())
            }

            if (b) {
                callback?.onSuccess(
                    checkedItem!!.latLonPoint.longitude,
                    checkedItem!!.latLonPoint.latitude,
                    checkedItem!!.title, checkedItem!!.snippet, path
                )
                finish()

            } else {
                Log.e("screenShot", "截屏失败")
            }

        } catch (e: FileNotFoundException) {
            Log.e("screenShot", e.printStackTrace().toString())
        }
    }

    override fun onMapScreenShot(p0: Bitmap?, p1: Int) {
    }


    private var checkedItem: PoiItem? = null
    override fun onClick(view: View) {
        when (view) {
            binding.backToMyLocationBtn -> {
                mLocationClient?.stopLocation()
                mLocationClient?.startLocation()
            }
            binding.barCl.rightTextBtn -> {
                if (callback != null) {
                    if (adapter.data.size > adapter.checkPosition) {
                        checkedItem = adapter.data[adapter.checkPosition]
                        screenShot()
                    }
                } else {
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

    private var mListener: LocationSource.OnLocationChangedListener? = null

    private fun stopLocate() {
        if (null != mLocationClient) {
            mLocationClient!!.stopLocation()
            mLocationClient!!.onDestroy()
            mLocationClient = null
        }
    }

    override fun deactivate() {
        LogUtils.d("deactivate===  ")

        mListener = null
        stopLocate()
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        LogUtils.d("activate===  ")
        mListener = p0
        initLocation()
    }

    override fun onMyLocationChange(p0: Location) {
        if (p0 is AMapLocation) {
            p0.apply {
                UserManager.saveLocation(
                    "${this.latitude}",
                    "${this.longitude}",
                    this.province,
                    this.city
                )
            }
        }

    }


}