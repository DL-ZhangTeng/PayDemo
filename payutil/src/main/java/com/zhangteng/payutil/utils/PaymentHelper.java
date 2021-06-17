package com.zhangteng.payutil.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhangteng.base.base.BaseApplication;
import com.zhangteng.base.utils.ToastUtils;
import com.zhangteng.payutil.wallet.RequestPayResultEventBus;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by swing on 2019/7/31 0031.
 */
public class PaymentHelper {
    private static final int SDK_PAY_FLAG = 1;

    /**
     * @param activity        调用的activity
     * @param weChatPayEntity 微信调用参数
     */
    public void startWeChatPay(Activity activity, WxChatPayEntity weChatPayEntity) {
        if (activity == null || weChatPayEntity == null) {
            return;
        }
        if (!WxPayConfig.APP_ID.equals(weChatPayEntity.getAppid())) {
            return;
        }
        IWXAPI wxapi = WXAPIFactory.createWXAPI(activity, WxPayConfig.APP_ID, true);
        // 将该app注册到微信
        wxapi.registerApp(WxPayConfig.APP_ID);
        PayReq req = new PayReq();
        req.appId = WxPayConfig.APP_ID;
        req.partnerId = weChatPayEntity.getPartnerid();
        req.prepayId = weChatPayEntity.getPrepayid();
        req.nonceStr = weChatPayEntity.getNoncestr();
        req.timeStamp = weChatPayEntity.getTimeStamp();
        req.packageValue = weChatPayEntity.getPackageValue();
        req.sign = weChatPayEntity.getSign();
        wxapi.sendReq(req);
    }

    /**
     * @param activity     调用的activity
     * @param alipayEntity 支付宝参数
     */
    public void startAliPayV2(final Activity activity, final AlipayEntity alipayEntity) {
        if (activity == null || alipayEntity == null) {
            return;
        }
        Runnable payRunnable = () -> {
            // 构造PayTask 对象
            PayTask alipay = new PayTask(activity);
            // 调用支付接口，获取支付结果
            if (TextUtils.isEmpty(alipayEntity.getPrePayOrderInfo())) {
                ToastUtils.Companion.showShort(activity, "无法获取支付信息");
                return;
            }
            Map<String, String> result = alipay.payV2(alipayEntity.getPrePayOrderInfo(), true);
            Log.i("msp", result.toString());

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        EventBus.getDefault().post(new RequestPayResultEventBus(1, 0));
                        Toast.makeText(BaseApplication.Companion.getInstance(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            EventBus.getDefault().post(new RequestPayResultEventBus(1, -3));
                            Toast.makeText(BaseApplication.Companion.getInstance(), "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            EventBus.getDefault().post(new RequestPayResultEventBus(1, -2));
                            Toast.makeText(BaseApplication.Companion.getInstance(), "用户取消支付", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            EventBus.getDefault().post(new RequestPayResultEventBus(1, -1));
                            Toast.makeText(BaseApplication.Companion.getInstance(), "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };
}
