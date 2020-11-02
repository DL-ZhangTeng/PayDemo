package com.zhangteng.payutil.wallet;

/**
 * Created by swing on 2019/9/18 0018.
 */
public class RequestPayResultEventBus {
    public String orderId;
    public int payType;
    public int payResult;

    /**
     * @param payResult 支付结果 0成功 -1支付出错 -2用户取消支付-3支付中
     */
    public RequestPayResultEventBus(int payType, int payResult) {
        this.payType = payType;
        this.payResult = payResult;
    }
}
