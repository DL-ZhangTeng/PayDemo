package com.zhangteng.payutil.http.response;

/**
 * 支付结果回调
 * Created by swing on 2019/9/19 0019.
 */
public class PayResultResponse extends BaseResponse {

    /**
     * result : {"payNo":"2019091922001411900551095880","payType":"AliPay","tradeStatus":"TRADE_SUCCESS"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * payNo : 2019091922001411900551095880
         * payType : AliPay
         * tradeStatus : TRADE_SUCCESS
         */

        private String payNo;
        private String payType;
        private String tradeStatus;

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getTradeStatus() {
            return tradeStatus;
        }

        public void setTradeStatus(String tradeStatus) {
            this.tradeStatus = tradeStatus;
        }
    }
}
