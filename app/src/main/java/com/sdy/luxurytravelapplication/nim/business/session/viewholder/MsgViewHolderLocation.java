package com.sdy.luxurytravelapplication.nim.business.session.viewholder;

import android.widget.TextView;

import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ui.imageview.MsgThumbImageView;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.sdy.luxurytravelapplication.nim.common.util.media.ImageUtil;
import com.sdy.luxurytravelapplication.nim.common.util.sys.ScreenUtil;
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderLocation extends MsgViewHolderBase {

    public MsgViewHolderLocation(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    public MsgThumbImageView mapView;

    public TextView addressText;

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_location;
    }

    @Override
    public void inflateContentView() {
        mapView = (MsgThumbImageView) view.findViewById(R.id.message_item_location_image);
        addressText = (TextView) view.findViewById(R.id.message_item_location_address);
    }

    @Override
    public void bindContentView() {
        final LocationAttachment location = (LocationAttachment) message.getAttachment();
        addressText.setText(location.getAddress());

        int[] bound = ImageUtil.getBoundWithLength(getLocationDefEdge(), R.drawable.nim_location_bk, true);
        int width = bound[0];
        int height = bound[1];

        setLayoutParams(width, height, mapView);
        setLayoutParams(width, (int) (0.38 * height), addressText);

        mapView.loadAsResource(R.drawable.nim_location_bk, R.drawable.nim_message_item_round_bg);
    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getLocationProvider() != null) {
            LocationAttachment location = (LocationAttachment) message.getAttachment();
            NimUIKitImpl.getLocationProvider().openMap(context, location.getLongitude(), location.getLatitude(), location.getAddress());
        }
    }

    public static int getLocationDefEdge() {
        return (int) (0.5 * ScreenUtil.screenWidth);
    }
}
