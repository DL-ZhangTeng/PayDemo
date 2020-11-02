package com.zhangteng.paydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhangteng.base.base.BaseApplication;
import com.zhangteng.payutil.R;
import com.zhangteng.payutil.constants.Constants;
import com.zhangteng.payutil.wallet.RequestPayResultEventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * @ClassName: WXPayEntryActivity
 * @Description: 请更改到与自己业务相关的包路径
 * @Author: Swing 763263311@qq.com
 * @Date: 2020/11/2 0002 下午 16:27
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        //0成功 -1支付出错 -2用户取消支付
        //-1支付出错可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    EventBus.getDefault().post(new RequestPayResultEventBus(2, 0));
                    Toast.makeText(BaseApplication.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    EventBus.getDefault().post(new RequestPayResultEventBus(2, -1));
                    Toast.makeText(BaseApplication.getInstance(), "支付出错", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -2:
                    EventBus.getDefault().post(new RequestPayResultEventBus(2, -2));
                    Toast.makeText(BaseApplication.getInstance(), "取消支付", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    }
}