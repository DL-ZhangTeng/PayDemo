package com.zhangteng.payutil.http.presenter;

import com.alibaba.fastjson.JSON;
import com.zhangteng.base.mvp.base.BaseHttpEntity;
import com.zhangteng.base.mvp.base.BasePresenter;
import com.zhangteng.base.utils.ToastUtils;
import com.zhangteng.payutil.http.model.PayModel;
import com.zhangteng.payutil.http.response.PayResultResponse;
import com.zhangteng.payutil.http.view.PayView;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.WxChatPayEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class PayPresenter extends BasePresenter<PayView> {
    private PayModel model;

    public PayPresenter() {
        this.model = new PayModel();
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    public void createPayOrder(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        model.createPayOrder(JSON.toJSONString(map), new BaseHttpEntity<AlipayEntity>(view.get()) {
            @Override
            public void onSuccess(AlipayEntity data) {
                view.get().aliPayCreateOrderFinish(data);
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                view.get().payResult(-1, goodsOrderId);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                view.get().payResult(1, goodsOrderId);
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                view.get().payResult(1, goodsOrderId);
            }
        });
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    public void createPayOrderOfWX(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        model.createPayOrderOfWX(JSON.toJSONString(map), new BaseHttpEntity<WxChatPayEntity>(view.get()) {
            @Override
            public void onSuccess(WxChatPayEntity data) {
                view.get().wxPayCreateOrderFinish(data);
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                view.get().payResult(-1, goodsOrderId);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                view.get().payResult(1, goodsOrderId);
                ToastUtils.showShort(view.get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                view.get().payResult(1, goodsOrderId);
                ToastUtils.showShort(view.get().getViewContext(), "网络异常");
            }
        });
    }

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    public void createPayOrderOfWallet(String goodsOrderId, int typeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("outTradeOrder", goodsOrderId);
        map.put("typeName", typeName);
        model.createPayOrderOfWallet(JSON.toJSONString(map), new BaseHttpEntity<Boolean>(view.get()) {
            @Override
            public void onSuccess(Boolean data) {
                view.get().walletPayCreateOrderFinish(0);
                ToastUtils.showShort(view.get().getViewContext(), "支付成功");
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                view.get().walletPayCreateOrderFinish(-1);
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                view.get().walletPayCreateOrderFinish(1);
                ToastUtils.showShort(view.get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                view.get().walletPayCreateOrderFinish(1);
                ToastUtils.showShort(view.get().getViewContext(), "网络异常");
            }
        });
    }

    /**
     * @param id        订单号
     * @param payType   支付方式, 1: 支付宝手机app支付 2： 微信
     * @param payResult 支付结果  0成功 -1支付出错 -2用户取消支付-3支付中
     */
    public void getPayResult(String id, int payType, int payResult) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("payType", payType);
        model.getPayResult(JSON.toJSONString(map), new BaseHttpEntity<PayResultResponse.ResultBean>(view.get()) {
            @Override
            public void onSuccess(PayResultResponse.ResultBean data) {
                view.get().payResult(0, data.getPayNo());
                if (payResult == -3) {
                    ToastUtils.showShort(view.get().getViewContext(), "支付成功");
                }
            }

            @Override
            public void onError(int code, String error) {
                super.onError(code, error);
                view.get().payResult(-1, null);
                if (payResult == -3) {
                    ToastUtils.showShort(view.get().getViewContext(), "支付失败");
                }
            }

            @Override
            public void onNoNetworkError() {
                super.onNoNetworkError();
                view.get().payResult(1, null);
                ToastUtils.showShort(view.get().getViewContext(), "无网络，请检测网络");
            }

            @Override
            public void onHttpError(int code, String error) {
                super.onHttpError(code, error);
                view.get().payResult(1, null);
                ToastUtils.showShort(view.get().getViewContext(), "网络异常，请稍后在订单列表查看结果");
            }
        });
    }

    /**
     * 获取钱包余额
     */
    public void getBalance() {
        model.getBalance(new BaseHttpEntity<BigDecimal>(view.get()) {
            @Override
            public void onSuccess(BigDecimal data) {
                if (data != null)
                    view.get().showbalance(data);
            }
        });
    }
}
