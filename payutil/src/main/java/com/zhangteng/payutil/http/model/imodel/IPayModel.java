package com.zhangteng.payutil.http.model.imodel;

import com.zhangteng.base.mvp.base.BaseHttpEntity;
import com.zhangteng.base.mvp.base.IModel;
import com.zhangteng.payutil.http.response.PayResultResponse;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.WxChatPayEntity;

import java.math.BigDecimal;

public interface IPayModel extends IModel {
    public void createPayOrder(String params, BaseHttpEntity<AlipayEntity> baseHttpEntity);

    public void createPayOrderOfWX(String params, BaseHttpEntity<WxChatPayEntity> baseHttpEntity);

    public void createPayOrderOfWallet(String params, BaseHttpEntity<Boolean> baseHttpEntity);

    public void getPayResult(String params, BaseHttpEntity<PayResultResponse.ResultBean> baseHttpEntity);

    public void getBalance(BaseHttpEntity<BigDecimal> baseHttpEntity);
}
