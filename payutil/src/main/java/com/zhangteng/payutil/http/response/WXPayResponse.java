package com.zhangteng.payutil.http.response;

import com.zhangteng.payutil.utils.WxChatPayEntity;

/**
 * Created by swing on 2019/8/13 0013.
 */
public class WXPayResponse extends BaseResponse {

    /**
     * result : {"package":"Sign=WXPay","appid":"wx93d356e921174dfd","sign":"42759C86FA90CE1E80891C1F7929FCB3","pre_pay_order_status":"SUCCESS","partnerid":"1551144091","prepayid":"wx27134822411395de8ba9869a1086576000","noncestr":"0df2f6cfd4914c73b6754d5f5cf9c1b5","timestamp":"1566884913"}
     */

    private WxChatPayEntity result;

    public WxChatPayEntity getResult() {
        return result;
    }

    public void setResult(WxChatPayEntity result) {
        this.result = result;
    }
}
