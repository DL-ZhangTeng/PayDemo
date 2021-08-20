package com.zhangteng.payutil.http.presenter.ipresenter;

import com.zhangteng.base.mvp.base.IPresenter;
import com.zhangteng.payutil.http.model.imodel.IPayModel;
import com.zhangteng.payutil.http.view.PayView;

public interface IPayPresenter extends IPresenter<PayView, IPayModel> {

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    public void createPayOrder(String goodsOrderId, int typeName);

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    public void createPayOrderOfWX(String goodsOrderId, int typeName);

    /**
     * @param goodsOrderId 订单号
     * @param typeName     各个模块名
     */
    public void createPayOrderOfWallet(String goodsOrderId, int typeName);

    /**
     * @param goodsOrderId 订单号
     */
    public void cancelOrder(String goodsOrderId);

    /**
     * @param id        订单号
     * @param payType   支付方式, 1: 支付宝手机app支付 2： 微信
     * @param payResult 支付结果  0成功 -1支付出错 -2用户取消支付-3支付中
     */
    public void getPayResult(String id, int payType, int payResult);

    /**
     * 获取钱包余额
     */
    public void getBalance();
}
