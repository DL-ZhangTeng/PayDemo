package com.zhangteng.payutil.constants;

/**
 * @ClassName: Constants
 * @Description: 常量
 * @Author: Swing 763263311@qq.com
 * @Date: 2020/11/2 0002 下午 15:28
 */
public interface Constants {
    /**
     * 支付宝注册id
     */
    String APP_ID = "";
    /**
     * 微信注册id
     */
    String WX_APPID = "";
    /**
     * 用户id,自己的业务请求
     */
    String USERID = "";

    String HOST = "http://192.168.1.1";
    int SUCCESS = 100;//操作成功
    int PERMISSION_DENIED = 401;//权限不够
    int DEBLOCKING = 423;//封号
    int SERVER_ERROR = 500;//服务维护中/异常
}
