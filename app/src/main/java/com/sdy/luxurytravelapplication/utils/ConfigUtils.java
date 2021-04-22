package com.sdy.luxurytravelapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.constant.Constants;
import com.sdy.luxurytravelapplication.ext.CommonFunction;
import com.sdy.luxurytravelapplication.ui.activity.PhoneActivity;
import com.sdy.luxurytravelapplication.ui.activity.VerifycodeActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;


public class ConfigUtils {
    /**
     * 闪验三网运营商授权页配置类
     *
     * @param context
     * @return
     */

    //沉浸式竖屏样式
    public static ShanYanUIConfig getCJSConfig(final Context context) {
        /************************************************自定义控件**************************************************************/
        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.shape_rectangle_green_25dp);
        Drawable backgruond = context.getResources().getDrawable(R.drawable.shanyan_demo_auth_no_bg);
        Drawable returnBg = context.getResources().getDrawable(R.drawable.icon_return_x);
        //loading自定义加载框
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout view_dialog = (LinearLayout) inflater.inflate(R.layout.loading_layout, null);
        RelativeLayout.LayoutParams mLayoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view_dialog.setLayoutParams(mLayoutParams3);
        view_dialog.setVisibility(View.GONE);


        LayoutInflater inflater1 = LayoutInflater.from(context);
        RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.shanyan_demo_other_login_item, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, 0, 0, SizeUtils.dp2px(68));
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.setLayoutParams(layoutParamsOther);
        otherLogin(context, relativeLayout);
        @SuppressLint("UseCompatLoadingForDrawables") ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                .setActivityTranslateAnim("shanyan_demo_fade_in_anim", "shanyan_dmeo_fade_out_anim")
                //授权页导航栏：
                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavReturnBtnWidth(24)
                .setNavReturnBtnHeight(24)
                .setNavReturnBtnOffsetX(15)
                .setAuthBGImgPath(backgruond)
                .setLogoHidden(true)   //是否隐藏logo
                .setDialogDimAmount(0f)
                .setNavReturnImgPath(returnBg)
                .setNavReturnImgHidden(false)
                .setFullScreen(false)
                .setStatusBarHidden(false)


                //授权页号码栏：
                .setNumberColor(Color.parseColor("#333333"))  //设置手机号码字体颜色
                .setNumFieldOffsetY(160)
                .setNumberSize(30)
                .setNumberBold(true)
                .setNumFieldHeight(42)


                //授权页登录按钮：
                .setLogBtnText(context.getString(R.string.one_key_login))  //设置登录按钮文字
                .setLogBtnTextColor(0xffffffff)   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnTextSize(16)
                .setLogBtnTextBold(true)
                .setLogBtnOffsetY(264)
                .setLogBtnHeight(50)
                .setLogBtnWidth(285)

                //授权页隐私栏：
                .setAppPrivacyOne(context.getResources().getString(R.string.user_protocol), Constants.PRIVACY_URL)  //设置开发者隐私条款1名称和URL(名称，url)
                .setAppPrivacyTwo(context.getResources().getString(R.string.privacy_protocol), Constants.PROTOCOL_URL)  //设置开发者隐私条款2名称和URL(名称，url)
                .setAppPrivacyColor(Color.parseColor("#FFC6CAD4"), Color.parseColor("#FFC6CAD4"))    //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
//                .setPrivacyText(context.getString(R.string.login_presents_you_agree), context.getString(R.string.and1), context.getString(R.string.and2), "、", context.getString(R.string.privacy_for_share))
                .setPrivacyText(context.getString(R.string.login_presents_you_agree), context.getString(R.string.and1), context.getString(R.string.and2), "、", "》")
                .setPrivacyOffsetBottomY(10)//设置隐私条款相对于屏幕下边缘y偏
                .setPrivacyState(true)
                .setPrivacyNameUnderline(true)
                .setPrivacyTextSize(10)
                .setPrivacyOffsetX(25)
                .setOperatorPrivacyAtLast(true)
                .setPrivacySmhHidden(true)
                .setCheckBoxHidden(false)
                .setCheckedImgPath(context.getResources().getDrawable(R.drawable.icon_pay_checked))
                .setUncheckedImgPath(context.getResources().getDrawable(R.drawable.icon_pay_normal))
                .setCheckBoxWH(16,16)
                .setPrivacyState(false)

                .setSloganHidden(false)
                .setSloganOffsetY(207)
                .setSloganTextColor(Color.parseColor("#FFC6CAD4"))
                .setSloganTextSize(12)

                .setShanYanSloganHidden(true)


//                 .addCustomView(numberLayout, false, false, null)

                .setLoadingView(view_dialog)
                // 添加自定义控件:
                .addCustomView(relativeLayout, false, false, null)
                //标题栏下划线，可以不写
                .build();

        return uiConfig;

    }

    private static void otherLogin(final Context context, RelativeLayout relativeLayout) {
        LinearLayout weixin = relativeLayout.findViewById(R.id.shanyan_dmeo_weixin);
        LinearLayout phone = relativeLayout.findViewById(R.id.shanyan_dmeo_phone);
        LinearLayout qq = relativeLayout.findViewById(R.id.shanyan_dmeo_qq);
        weixin.setOnClickListener(v -> {
            phone.setEnabled(false);
            CommonFunction.INSTANCE.socialLogin(context, SHARE_MEDIA.WEIXIN);
            phone.postDelayed(() -> phone.setEnabled(true), 1000L);
        });
        phone.setOnClickListener(v -> {
            weixin.setEnabled(false);

            PhoneActivity.Companion.startToPhone(context, VerifycodeActivity.TYPE_LOGIN_PHONE);
            weixin.postDelayed(() -> weixin.setEnabled(true), 1000L);
        });

        qq.setOnClickListener(v -> {
            CommonFunction.INSTANCE.socialLogin(context, SHARE_MEDIA.QQ);
            qq.postDelayed(() -> qq.setEnabled(true), 1000L);

        });

    }
}
