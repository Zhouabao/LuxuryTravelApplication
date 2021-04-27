package com.sdy.luxurytravelapplication.nim.business.session.viewholder;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.business.session.activity.WatchMessagePictureActivity;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderPicture extends MsgViewHolderThumbBase {
    private LinearLayout message_location_ll;
    private ImageView locationMarker;
    private TextView locatioName, locationAddress;

    public MsgViewHolderPicture(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
//      if (isLocationImage())
//        return R.layout.nim_message_item_location;
//      else
        return R.layout.nim_message_item_picture;
    }

    @Override
    public void inflateContentView() {
        super.inflateContentView();
        message_location_ll = findViewById(R.id.message_location_ll);
        locatioName = findViewById(R.id.message_location_name);
        locationAddress = findViewById(R.id.message_location_address);
        locationMarker = findViewById(R.id.location_marker);
    }

    @Override
    public void bindContentView() {
        super.bindContentView();
        if (isLocationImage()) {
            setLayoutParams(SizeUtils.dp2px(237F), SizeUtils.dp2px(90F), thumbnail);
            setLayoutParams(SizeUtils.dp2px(237F), ViewGroup.LayoutParams.WRAP_CONTENT, message_location_ll);
            message_location_ll.setVisibility(View.VISIBLE);
            locationMarker.setVisibility(View.GONE);
            locatioName.setText(getLocationName());
            locationAddress.setText(getLocationAddress());
        } else {
            locationMarker.setVisibility(View.GONE);
            message_location_ll.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemClick() {
//        if (isLocationImage())
//            LocationActivity.Companion.startedLocated(context,message);
//        else
        WatchMessagePictureActivity.Companion.start(context, message, false);

    }

    @Override
    protected int maskBg() {
        if (isLocationImage())
            return R.drawable.nim_message_item_bottom_round_bg;
        else
//            return 0;
            return R.drawable.nim_message_item_round_bg;

    }

    @Override
    protected String thumbFromSourceFile(String path) {
        return path;
    }

    private boolean isLocationImage() {
        if (message.getRemoteExtension() != null && message.getRemoteExtension().get("latitude") != null) {
            return true;
        }
        return false;
    }


    private Double getLocationLatitude() {
        if (message.getRemoteExtension() != null && message.getRemoteExtension().get("latitude") != null)
            return (Double) message.getRemoteExtension().get("latitude");
        else
            return 0.0;
    }

    private Double getLocationLongitude() {
        if (message.getRemoteExtension() != null && message.getRemoteExtension().get("longitude") != null)
            return (Double) message.getRemoteExtension().get("longitude");
        else
            return 0.0;
    }

    private String getLocationAddress() {
        if (message.getRemoteExtension() != null && message.getRemoteExtension().get("address") != null)
            return (String) message.getRemoteExtension().get("address");
        else
            return "";

    }

    private String getLocationName() {
        if (message.getRemoteExtension() != null && message.getRemoteExtension().get("name") != null)
            return (String) message.getRemoteExtension().get("name");
        else
            return "";
    }


    @Override
    protected int leftBackground() {
        return R.drawable.shape_rectangle_gray_top_15dp;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.shape_rectangle_gray_top_15dp;
    }
}
