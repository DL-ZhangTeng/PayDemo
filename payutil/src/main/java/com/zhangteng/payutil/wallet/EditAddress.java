package com.zhangteng.payutil.wallet;

/**
 * Created by swing on 2019/8/12 0012.
 */
public class EditAddress {
    public EditAddress() {
    }

    public EditAddress(String consignee, String id, String phone, String zylAddress) {
        this.consignee = consignee;
        this.id = id;
        this.phone = phone;
        this.zylAddress = zylAddress;
    }

    public String consignee;
    public String id;
    public String phone;
    public String zylAddress;
}
