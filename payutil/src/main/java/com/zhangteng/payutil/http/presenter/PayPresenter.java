package com.zhangteng.payutil.http.presenter;

import com.google.gson.Gson;
import com.zhangteng.base.mvp.base.BaseHttpEntity;
import com.zhangteng.base.mvp.base.BasePresenter;
import com.zhangteng.base.utils.ToastUtils;
import com.zhangteng.payutil.http.model.PayModel;
import com.zhangteng.payutil.http.model.imodel.IPayModel;
import com.zhangteng.payutil.http.presenter.ipresenter.IPayPresenter;
import com.zhangteng.payutil.http.response.PayResultResponse;
import com.zhangteng.payutil.http.view.PayView;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.WxChatPayEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class PayPresenter extends BasePresenter<PayView, IPayModel> implements IPayPresenter {

    public PayPresenter() {
        setMModel(new PayModel());
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    @Override
    public void createPayOrder(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        getMModel().createPayOrder(new Gson().toJson(map), new BaseHttpEntity<AlipayEntity>(getMView().get()) {
            @Override
            public void onSuccess(AlipayEntity data) {
                getMView().get().aliPayCreateOrderFinish(data);
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                getMView().get().payResult(-1, goodsOrderId);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                getMView().get().payResult(1, goodsOrderId);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                getMView().get().payResult(1, goodsOrderId);
            }
        });
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    @Override
    public void createPayOrderOfWX(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        getMModel().createPayOrderOfWX(new Gson().toJson(map), new BaseHttpEntity<WxChatPayEntity>(getMView().get()) {
            @Override
            public void onSuccess(WxChatPayEntity data) {
                getMView().get().wxPayCreateOrderFinish(data);
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                getMView().get().payResult(-1, goodsOrderId);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                getMView().get().payResult(1, goodsOrderId);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                getMView().get().payResult(1, goodsOrderId);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "网络异常");
            }
        });
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    @Override
    public void createPayOrderOfWallet(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        getMModel().createPayOrderOfWallet(new Gson().toJson(map), new BaseHttpEntity<Boolean>(getMView().get()) {
            @Override
            public void onSuccess(Boolean data) {
                getMView().get().walletPayCreateOrderFinish(0);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "支付成功");
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                getMView().get().walletPayCreateOrderFinish(-1);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                getMView().get().walletPayCreateOrderFinish(1);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                getMView().get().walletPayCreateOrderFinish(1);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "网络异常");
            }
        });
    }

    /**
     * @param id        订单号
     * @param payType   支付方式, 1: 支付宝手机app支付 2： 微信
     * @param payResult 支付结果  0成功 -1支付出错 -2用户取消支付-3支付中
     */
    @Override
    public void getPayResult(String id, int payType, int payResult) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("payType", payType);
        getMModel().getPayResult(new Gson().toJson(map), new BaseHttpEntity<PayResultResponse.ResultBean>(getMView().get()) {
            @Override
            public void onSuccess(PayResultResponse.ResultBean data) {
                getMView().get().payResult(0, data.getPayNo());
                if (payResult == -3) {
                    ToastUtils.Companion.showShort(getMView().get().getViewContext(), "支付成功");
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                getMView().get().payResult(-1, null);
                if (payResult == -3) {
                    ToastUtils.Companion.showShort(getMView().get().getViewContext(), "支付失败");
                }
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                getMView().get().payResult(1, null);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                getMView().get().payResult(1, null);
                ToastUtils.Companion.showShort(getMView().get().getViewContext(), "网络异常，请稍后在订单列表查看结果");
            }
        });
    }

    /**
     * 获取钱包余额
     */
    @Override
    public void getBalance() {
        getMModel().getBalance(new BaseHttpEntity<BigDecimal>(getMView().get()) {
            @Override
            public void onSuccess(BigDecimal data) {
                if (data != null)
                    getMView().get().showbalance(data);
            }
        });
    }
}
