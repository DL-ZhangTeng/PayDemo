package com.zhangteng.payutil.http.response;


import java.math.BigDecimal;

public class BalanceResponse extends BaseResponse {

    /**
     * result : {"balance":0,"userId":"string"}
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
         * balance : 0
         * userId : string
         */

        private BigDecimal balance;
        private String userId;

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
