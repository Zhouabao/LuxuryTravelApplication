package com.sdy.luxurytravelapplication.nim.business.session.actions;

import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider;
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class LocationAction extends BaseAction {
    private final static String TAG = "LocationAction";


    public LocationAction() {
    }

    @Override
    public void onClick() {
        if (NimUIKitImpl.getLocationProvider() != null) {
            NimUIKitImpl.getLocationProvider().requestLocation(getActivity(), new LocationProvider.Callback() {
                @Override
                public void onSuccess(double longitude, double latitude, String address) {
                    IMMessage message = MessageBuilder.createLocationMessage(getAccount(), getSessionType(), latitude, longitude, address);
                    sendMessage(message);
                }
            });
        }
    }
}
