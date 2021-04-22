package com.sdy.luxurytravelapplication.nim.wrapper;


import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.activity.ToolBarOptions;

/**
 * Created by hzxuwen on 2016/6/16.
 */
public class NimToolBarOptions extends ToolBarOptions {

    public NimToolBarOptions() {
        logoId = 0;
//        logoId = R.drawable.nim_actionbar_nest_dark_logo;
        navigateId = R.drawable.icon_return_arrow;
        isNeedNavigate = true;
    }

    public NimToolBarOptions(int logoId) {
        this.logoId = logoId;
        navigateId = R.drawable.icon_return_arrow;
        isNeedNavigate = true;
    }
}
