package com.sdy.luxurytravelapplication.api

import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by chenxz on 2018/4/21.
 */
interface ApiService {

    /**
     * 获取登录配置开关信息
     */
    @POST("OpenApi/getVersion${Constants.END_BASE_URL}")
    fun getRegisterProcessType(): Observable<BaseResp<RegisterFileBean>>

    /**
     * 注册发送短信
     * scene :场景 register 注册/登录 change 变更 cancel注销
     * phone :电话号码
     */
    @FormUrlEncoded
    @POST("OpenApi/SendSms${Constants.END_BASE_URL}")
    fun sendSms(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>



    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("OpenApi/LoginOrAlloc${Constants.END_BASE_URL}")
    fun loginOrAlloc(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<LoginBean>>


    /**
     * 注销账号
     */
    @FormUrlEncoded
    @POST("Account/cancelAccount${Constants.END_BASE_URL}")
    fun cancelAccount(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>



    /**
     * 注册填写个人信息
     */
    @FormUrlEncoded
    @POST("MemberInfo/setPersonal${Constants.END_BASE_URL}")
    fun setPersonal(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>>



    /**
     * 退出登录
     * http://www.wanandroid.com/user/logout/json
     */
    @GET("user/logout/json")
    fun logout(): Observable<BaseResp<Any>>


    /**
     * 获取个人积分，需要登录后访问
     * https://www.wanandroid.com/lg/coin/userinfo/json
     */
    @GET("/lg/coin/userinfo/json")
    fun getUserInfo(): Observable<BaseResp<Any>>


    /**
     * 微信绑定
     */
    @POST("Account/bundSocial${Constants.END_BASE_URL}")
    @FormUrlEncoded
    fun bundSocial(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 微信绑定
     */
    @POST("Account/bundSocial${Constants.END_BASE_URL}")
    @FormUrlEncoded
    fun register(@FieldMap params: HashMap<String, String>): Observable<BaseResp<Any>>

}