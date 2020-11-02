package com.zhangteng.payutil.http.view;

import android.content.Context;

import com.zhangteng.base.mvp.base.BaseLoadingView;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.WxChatPayEntity;

import java.math.BigDecimal;


public interface PayView extends BaseLoadingView<Object> {
    Context getViewContext();

    void createPayOrder(String orderId);

    void cancelPayOrder(String orderId);

    void aliPayCreateOrderFinish(AlipayEntity alipayEntity);

    void wxPayCreateOrderFinish(WxChatPayEntity wxChatPayEntity);

    void walletPayCreateOrderFinish(int payResult);

    void payResult(int payResult, String payNo);

    void showbalance(BigDecimal balance);

    @Override
    default void inflateView(Object data) {
    }
}
