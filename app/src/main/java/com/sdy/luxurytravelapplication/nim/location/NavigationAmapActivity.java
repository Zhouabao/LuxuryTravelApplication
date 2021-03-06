package com.sdy.luxurytravelapplication.nim.location;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ToastHelper;
import com.sdy.luxurytravelapplication.nim.common.activity.ToolBarOptions;
import com.sdy.luxurytravelapplication.nim.common.activity.UI;
import com.sdy.luxurytravelapplication.nim.common.util.string.StringUtil;
import com.sdy.luxurytravelapplication.nim.location.helper.NimLocationManager;
import com.sdy.luxurytravelapplication.nim.location.model.NimLocation;
import com.sdy.luxurytravelapplication.nim.wrapper.NimToolBarOptions;

public class NavigationAmapActivity extends UI implements
        LocationExtras, NimLocationManager.NimLocationListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {

    private TextView sendButton;
    private MapView mapView;

    private NimLocationManager locationManager = null;

    private LatLng myLatLng;
    private LatLng desLatLng;

    private Marker myMaker;
    private Marker desMaker;

    private String myAddressInfo; // 对应的地址信息
    private String desAddressInfo; // 目的地址信息

    private boolean firstLocation = true;
    private boolean firstTipLocation = true;

    private String myLocationFormatText;

    AMap amap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_amap_navigation_layout);
        mapView = (MapView) findViewById(R.id.autonavi_mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);

        initView();
        initAmap();
        initLocation();
        updateSendStatus();
    }

    private void initView() {
        sendButton = findView(R.id.action_bar_right_clickable_textview);
        sendButton.setText(R.string.location_navigate);
        sendButton.setVisibility(View.INVISIBLE);
        myLocationFormatText = getString(R.string.format_mylocation);
    }

    private void initAmap() {
        try {
            amap = mapView.getMap();

            UiSettings uiSettings = amap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

            amap.setOnMarkerClickListener(this); // 标记点击
            amap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
            amap.setInfoWindowAdapter(this); // 必须 信息窗口显示

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLocation() {
        locationManager = new NimLocationManager(this, this);
        Location location = locationManager.getLastKnownLocation();

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(LATITUDE, -100);
        double longitude = intent.getDoubleExtra(LONGITUDE, -100);
        desLatLng = new LatLng(latitude, longitude);

        desAddressInfo = intent.getStringExtra(ADDRESS);
        if (TextUtils.isEmpty(desAddressInfo)) {
            desAddressInfo = getString(R.string.location_address_unkown);
        }

        float zoomLevel = intent.getIntExtra(ZOOM_LEVEL, DEFAULT_ZOOM_LEVEL);

        if (location == null) {
            myLatLng = new LatLng(39.90923, 116.397428);
        } else {
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        createNavigationMarker();
        startLocationTimeout();

        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(desLatLng, zoomLevel, 0, 0));
        amap.moveCamera(camera);
    }

    private void startLocationTimeout() {
        Handler handler = getHandler();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 20 * 1000);// 20s超时
    }

    private void updateSendStatus() {
        if (isFinishing()) {
            return;
        }
        if (TextUtils.isEmpty(myAddressInfo)) {
            setTitle(R.string.location_loading);
            sendButton.setVisibility(View.GONE);
        } else {
            setTitle(R.string.location_map);
            sendButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationManager != null) {
            locationManager.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        locationManager.request();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationManager != null) {
            locationManager.stop();
        }
    }

    private void navigate() {
        NimLocation des = new NimLocation(desLatLng.latitude, desLatLng.longitude);
        NimLocation origin = new NimLocation(myLatLng.latitude, myLatLng.longitude);
    }

    @Override
    public void onLocationChanged(NimLocation location) {
        if (location != null && location.hasCoordinates()) {
            if (firstLocation) {
                firstLocation = false;
                myAddressInfo = location.getFullAddr();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                myLatLng = new LatLng(latitude, longitude);
                // 缩放到可见区
                int boundPadding = getResources().getDimensionPixelSize(R.dimen.friend_map_bound_padding);
                LatLngBounds bounds = LatLngBounds.builder().include(myLatLng).include(desLatLng).build();
                CameraUpdate camera = CameraUpdateFactory.newLatLngBounds(bounds, boundPadding);
                amap.moveCamera(camera);
                updateMyMarkerLatLng();

                updateSendStatus();
            }
        } else {
            showLocationFailTip();
        }
        clearTimeoutHandler();
    }

    private void updateMyMarkerLatLng() {
        myMaker.setPosition(myLatLng);
        myMaker.showInfoWindow();
    }

    private void showLocationFailTip() {
        if (firstLocation && firstTipLocation) {
            firstTipLocation = false;
            myAddressInfo = getString(R.string.location_address_unkown);
            ToastHelper.showToast(this, R.string.location_address_fail);
        }
    }

    private void clearTimeoutHandler() {
        Handler handler = getHandler();
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showLocationFailTip();
            updateSendStatus();
        }
    };

    private MarkerOptions defaultMarkerOptions() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
        markerOptions.position(desLatLng);
        return markerOptions;
    }

    private void createNavigationMarker() {
        desMaker = amap.addMarker(defaultMarkerOptions());
        desMaker.setPosition(desLatLng);
        desMaker.setTitle(desAddressInfo);
        desMaker.showInfoWindow();

        myMaker = amap.addMarker(defaultMarkerOptions());
        myMaker.setPosition(myLatLng);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == null) {
            return false;
        }
        String text = null;
        if (marker.equals(desMaker)) {
            text = desAddressInfo;
        } else if (marker.equals(myMaker)) {
            text = myAddressInfo;
        }
        if (!TextUtils.isEmpty(text)) {
            marker.setTitle(text);
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public View getInfoContents(Marker pmarker) {
        return getMarkerInfoView(pmarker);
    }

    @Override
    public View getInfoWindow(Marker pmarker) {
        return getMarkerInfoView(pmarker);
    }

    private View getMarkerInfoView(Marker pmarker) {
        String text = null;
        if (pmarker.equals(desMaker)) {
            text = desAddressInfo;
        } else if (pmarker.equals(myMaker)) {
            if (!StringUtil.isEmpty(myAddressInfo)) {
                text = String.format(myLocationFormatText, myAddressInfo);
            }
        }
        if (StringUtil.isEmpty(text)) {
            return null;
        }
        View view = getLayoutInflater().inflate(R.layout.amap_marker_window_info, null);
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(text);
        return view;
    }

}
