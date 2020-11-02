package com.zhangteng.payutil.http;

import com.zhangteng.payutil.http.response.AliPayResponse;
import com.zhangteng.payutil.http.response.BalanceResponse;
import com.zhangteng.payutil.http.response.PayResultResponse;
import com.zhangteng.payutil.http.response.WXPayResponse;
import com.zhangteng.payutil.http.response.WalletPayResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by swing on 2019/7/16 0002.
 */
public interface PayUtilApi {
    String MediaType_Name = "Content-type:";
    String MediaType_Form = "multipart/form-data";
    String MediaType_Json = "application/json;charset=UTF-8";
    String Secret_Name = "_secret:";
    String Secret_True = "true";
    String Secret_False = "false";
    String Cache_Name = "cache:";
    String Cache_One_Minute = "60";
    String Cache_One_Hour = "3600";
    String Cache_One_Day = "43200";
    String Cache_One_Week = "302400";
    String Cache_One_Month = "1296000";
    String Cache_Perpetual = "2147483647";

    //<editor-fold desc="支付支付宝统一下单">
    @Headers(value = {MediaType_Name + MediaType_Json, Secret_Name + Secret_True})
    @POST("/pay/aliPay/createAppOrder")
    Observable<AliPayResponse> createPayOrder(@Body RequestBody requestBody);
    //</editor-fold>

    //<editor-fold desc="支付微信统一下单">
    @Headers(value = {MediaType_Name + MediaType_Json, Secret_Name + Secret_True})
    @POST("/pay/weChat/pay")
    Observable<WXPayResponse> createWXOrder(@Body RequestBody requestBody);
    //</editor-fold>

    //<editor-fold desc="钱包支付">
    @Headers(value = {MediaType_Name + MediaType_Json, Secret_Name + Secret_True})
    @POST("/pay/wallet/walletPay")
    Observable<WalletPayResponse> createPayOrderOfWallet(@Body RequestBody requestBody);
    //</editor-fold>

    //<editor-fold desc="支付查询订单支付结果">
    @Headers(value = {MediaType_Name + MediaType_Json})
    @POST("/pay/getPayResult")
    Observable<PayResultResponse> getPayResult(@Body RequestBody requestBody);
    //</editor-fold>

    //<editor-fold desc="查询钱包余额">
    @GET("/pay/wallet/getBalance")
    Observable<BalanceResponse> getBalance(@Query(value = "userId") String userId);
    //</editor-fold>
}
