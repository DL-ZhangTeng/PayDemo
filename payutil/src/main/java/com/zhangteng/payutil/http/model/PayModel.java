package com.zhangteng.payutil.http.model;

import com.zhangteng.base.mvp.base.BaseHttpEntity;
import com.zhangteng.base.mvp.base.BaseModel;
import com.zhangteng.rxhttputils.http.GlobalHttpUtils;
import com.zhangteng.payutil.constants.Constants;
import com.zhangteng.payutil.http.PayUtilApi;
import com.zhangteng.payutil.http.response.AliPayResponse;
import com.zhangteng.payutil.http.response.BalanceResponse;
import com.zhangteng.payutil.http.response.PayResultResponse;
import com.zhangteng.payutil.http.response.Request;
import com.zhangteng.payutil.http.response.WXPayResponse;
import com.zhangteng.payutil.http.response.WalletPayResponse;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.WxChatPayEntity;

import java.math.BigDecimal;

import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 支付宝微信支付model
 * Created by swing on 2019/8/13 0013.
 */
public class PayModel extends BaseModel {

    public void createPayOrder(String params, BaseHttpEntity<AlipayEntity> baseHttpEntity) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(PayUtilApi.MediaType_Json), params);
        new Request<AliPayResponse>().request(GlobalHttpUtils.getInstance().createService(PayUtilApi.class).createPayOrder(requestBody), new BaseHttpEntity<AliPayResponse>() {
            @Override
            public void onSuccess(AliPayResponse response) {
                baseHttpEntity.onSuccess(response.getResult());
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                baseHttpEntity.onNoNetworkError();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                baseHttpEntity.onError(code, error);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                baseHttpEntity.onHttpError(code, error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                baseHttpEntity.onFinish();
            }
        });
    }

    public void createPayOrderOfWX(String params, BaseHttpEntity<WxChatPayEntity> baseHttpEntity) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(PayUtilApi.MediaType_Json), params);
        new Request<WXPayResponse>().request(GlobalHttpUtils.getInstance().createService(PayUtilApi.class).createWXOrder(requestBody), new BaseHttpEntity<WXPayResponse>() {
            @Override
            public void onSuccess(WXPayResponse response) {
                baseHttpEntity.onSuccess(response.getResult());
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                baseHttpEntity.onNoNetworkError();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                baseHttpEntity.onError(code, error);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                baseHttpEntity.onHttpError(code, error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                baseHttpEntity.onFinish();
            }
        });
    }

    public void createPayOrderOfWallet(String params, BaseHttpEntity<Boolean> baseHttpEntity) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(PayUtilApi.MediaType_Json), params);
        new Request<WalletPayResponse>().request(GlobalHttpUtils.getInstance().createService(PayUtilApi.class).createPayOrderOfWallet(requestBody), new BaseHttpEntity<WalletPayResponse>() {
            @Override
            public void onSuccess(WalletPayResponse response) {
                baseHttpEntity.onSuccess(true);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                baseHttpEntity.onNoNetworkError();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                baseHttpEntity.onError(code, error);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                baseHttpEntity.onHttpError(code, error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                baseHttpEntity.onFinish();
            }
        });
    }

    public void getPayResult(String params, BaseHttpEntity<PayResultResponse.ResultBean> baseHttpEntity) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(PayUtilApi.MediaType_Json), params);
        new Request<PayResultResponse>().request(GlobalHttpUtils.getInstance().createService(PayUtilApi.class).getPayResult(requestBody), new BaseHttpEntity<PayResultResponse>() {
            @Override
            public void onSuccess(PayResultResponse response) {
                baseHttpEntity.onSuccess(response.getResult());
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                baseHttpEntity.onNoNetworkError();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                baseHttpEntity.onError(code, error);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                baseHttpEntity.onHttpError(code, error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                baseHttpEntity.onFinish();
            }
        });
    }

    public void getBalance(BaseHttpEntity<BigDecimal> baseHttpEntity) {
        new Request<BalanceResponse>().request(GlobalHttpUtils.getInstance().createService(PayUtilApi.class).getBalance(Constants.USERID), new BaseHttpEntity<BalanceResponse>() {
            @Override
            public void onSuccess(BalanceResponse response) {
                if (response != null && response.getResult() != null)
                    baseHttpEntity.onSuccess(response.getResult().getBalance());
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                baseHttpEntity.onNoNetworkError();
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                baseHttpEntity.onError(code, error);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                baseHttpEntity.onHttpError(code, error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                baseHttpEntity.onFinish();
            }
        });
    }
}
