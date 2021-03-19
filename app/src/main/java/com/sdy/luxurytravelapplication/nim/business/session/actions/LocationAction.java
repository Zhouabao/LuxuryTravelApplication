package com.sdy.luxurytravelapplication.nim.business.session.actions;

import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider;
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl;
import com.sdy.luxurytravelapplication.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class LocationAction extends BaseAction {
    private final static String TAG = "LocationAction";


    public LocationAction() {
    }

    @Override
    public void onClick() {
        //msg.remoteExt = @{@"name": POIModel.name, @"address": POIModel.address, @"latitude": @(POIModel.location.latitude), @"longitude": @(POIModel.location.longitude)};
        if (NimUIKitImpl.getLocationProvider() != null) {
            NimUIKitImpl.getLocationProvider().requestLocation(getActivity(), new LocationProvider.Callback() {
                @Override
                public void onSuccess(double longitude, double latitude,String name, String address,String path ) {

                    File file =new  File(path);
                    if (!file.exists()) {
                        ToastUtil.INSTANCE.toast(getActivity().getString(R.string.file_unexist));
                    } else {
                        IMMessage message = MessageBuilder.createImageMessage(getAccount(), getSessionType(), file,file.getName());
                        HashMap<String,Object> map  =new HashMap<String, Object>();
                        map.put("name", name);
                        map.put("address", address);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        message.setRemoteExtension(map);

                        sendMessage(message);
                    }

//                    IMMessage message = MessageBuilder.createLocationMessage(getAccount(), getSessionType(), latitude, longitude, address);
                }
            });
        }
    }
}
