package com.zhangteng.payutil;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhangteng.base.base.BaseApplication;
import com.zhangteng.payutil.constants.Constants;
import com.zhangteng.rxhttputils.http.HttpUtils;

/**
 * Created by swing on 2018/8/2.
 */
public class PayApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initModuleApp(this);
        initModuleData(this);
    }

    @Override
    public void initModuleApp(Application application) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(application, null);
        msgApi.registerApp(Constants.WX_APPID);
        HttpUtils.init(this);
        HttpUtils.getInstance().ConfigGlobalHttpUtils()
                .getOkHttpClientBuilder();
        HttpUtils.getInstance()
                .ConfigGlobalHttpUtils()
                //全局的BaseUrl
                .setBaseUrl(Constants.HOST)
                //全局持久话cookie,保存本地每次都会携带在header中
                .setCookie(false)
                //全局超时配置
                .setReadTimeOut(10)
                //全局超时配置
                .setWriteTimeOut(10)
                //全局超时配置
                .setConnectionTimeOut(10)
                //全局是否打开请求log日志
                .setLog(true);
    }

    @Override
    public void initModuleData(Application application) {

    }
}
