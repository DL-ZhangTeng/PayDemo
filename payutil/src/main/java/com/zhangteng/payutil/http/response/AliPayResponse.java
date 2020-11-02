package com.zhangteng.payutil.http.response;

import com.zhangteng.payutil.utils.AlipayEntity;

/**
 * Created by swing on 2019/8/13 0013.
 */
public class AliPayResponse extends BaseResponse {
    private AlipayEntity result;

    public AlipayEntity getResult() {
        return result;
    }

    public void setResult(AlipayEntity result) {
        this.result = result;
    }
}
