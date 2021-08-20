package com.zhangteng.payutil.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhangteng.base.base.BaseActivity;
import com.zhangteng.base.utils.LoadViewHelper;
import com.zhangteng.payutil.R;
import com.zhangteng.payutil.http.presenter.PayPresenter;
import com.zhangteng.payutil.http.view.PayView;
import com.zhangteng.payutil.utils.AlipayEntity;
import com.zhangteng.payutil.utils.PaymentHelper;
import com.zhangteng.payutil.utils.WxChatPayEntity;
import com.zhangteng.payutil.wallet.RequestPayResultEventBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 继承PayActivity快速实现支付功能，实现的activity必须保函wallet_activity_zhifu_button.xml、wallet_activity_zhifu_content.xml中的所有id，确认取消按钮已实现点击事件
 *
 * @author Swing
 * @date 2019-06-20
 */
public abstract class PayActivity extends BaseActivity implements PayView {
    private RadioButton walletDialogWalletRb;
    private TextView walletDialogWalletBalance;
    private TextView walletDialogTishiTv;
    private RadioButton walletDialogAliRb;
    private RadioButton walletDialogWxRb;
    private TextView walletDialogMoneyTv;
    private TextView cancel;
    private TextView confirm;

    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 支付金额
     */
    private float amount;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 各个模块名
     */
    private int typeName;

    private PayPresenter payPresenter;
    private OnItemOnClickListener onItemOnClickListener;
    private OnPayResultListener onPayResultListener;
    private OnConfirmPayListener onConfirmPayListener;
    private OnCancelPayListener onCancelPayListener;

    private LoadViewHelper loadViewHelper;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        walletDialogWalletRb = findViewById(R.id.rb_pay_balance);
        walletDialogWalletBalance = findViewById(R.id.tv_pay_balance);
        walletDialogTishiTv = findViewById(R.id.rb_pay_balance_tishi);
        walletDialogAliRb = findViewById(R.id.rb_pay_Ali);
        walletDialogWxRb = findViewById(R.id.rb_pay_wx);
        walletDialogMoneyTv = findViewById(R.id.tv_order_totalPrice);
        cancel = findViewById(R.id.tv_order_cancel);
        confirm = findViewById(R.id.tv_order_confirm);

        payPresenter = new PayPresenter();
        payPresenter.attachView(this);
        payPresenter.onStart();
        cancel.setOnClickListener(view1 -> {
            if (onCancelPayListener != null) onCancelPayListener.onCancel(orderId);
            if (!TextUtils.isEmpty(orderId)) {
                cancelPayOrder(orderId);
            }
        });
        confirm.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(orderId))
                createPayOrder(orderId);
            if (onConfirmPayListener != null) onConfirmPayListener.onConfirm(orderId);
        });
        walletDialogWalletRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogAliRb.setChecked(false);
                walletDialogWxRb.setChecked(false);
                if (onItemOnClickListener != null)
                    onItemOnClickListener.OnItemClicked(walletDialogWalletRb, 0);
            }
        });
        walletDialogAliRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogWalletRb.setChecked(false);
                walletDialogWxRb.setChecked(false);
                if (onItemOnClickListener != null)
                    onItemOnClickListener.OnItemClicked(walletDialogAliRb, 1);
            }
        });
        walletDialogWxRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogWalletRb.setChecked(false);
                walletDialogAliRb.setChecked(false);
                if (onItemOnClickListener != null)
                    onItemOnClickListener.OnItemClicked(walletDialogWxRb, 2);
            }
        });
        payPresenter.getBalance();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payPresenter.detachView();
        payPresenter.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 订单id
     */
    public PayActivity setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * 各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    public PayActivity setTypeName(int typeName) {
        this.typeName = typeName;
        return this;
    }

    /**
     * 设置应支付金额
     *
     * @param amount 支付金额
     */
    public PayActivity setPaymentAmount(float amount) {
        this.amount = amount;
        walletDialogMoneyTv.setText(new DecimalFormat("0.00").format(amount));
        return this;
    }

    /**
     * 设置余额
     *
     * @param balance 余额
     */
    public PayActivity setBalance(BigDecimal balance) {
        this.balance = balance;
        initTishi();
        walletDialogWalletBalance.setText(String.format("余额支付(¥%s)", balance.toString()));
        return this;
    }

    /**
     * 初始化提示信息
     */
    private void initTishi() {
        if (balance == null || amount > balance.doubleValue()) {
            walletDialogTishiTv.setVisibility(View.VISIBLE);
            walletDialogWalletRb.setClickable(false);
            walletDialogWalletRb.setChecked(false);
        } else {
            walletDialogTishiTv.setVisibility(View.GONE);
            walletDialogWalletRb.setClickable(true);
            walletDialogWalletRb.setChecked(true);
        }
    }

    public PayActivity setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
        return this;
    }

    public PayActivity setOnPayResultListener(OnPayResultListener onPayResultListener) {
        this.onPayResultListener = onPayResultListener;
        return this;
    }

    public PayActivity setOnConfirmPayListener(OnConfirmPayListener onConfirmPayListener) {
        this.onConfirmPayListener = onConfirmPayListener;
        return this;
    }

    public PayActivity setOnCancelPayListener(OnCancelPayListener onCancelPayListener) {
        this.onCancelPayListener = onCancelPayListener;
        return this;
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void createPayOrder(String orderId) {
        if (walletDialogWalletRb.isChecked()) {
            payPresenter.createPayOrderOfWallet(orderId, typeName);
        } else if (walletDialogAliRb.isChecked()) {
            payPresenter.createPayOrder(orderId, typeName);
        } else if (walletDialogWxRb.isChecked()) {
            payPresenter.createPayOrderOfWX(orderId, typeName);
        }
    }

    @Override
    public void cancelPayOrder(String orderId) {
        payPresenter.cancelOrder(orderId);
    }

    @Override
    public void aliPayCreateOrderFinish(AlipayEntity alipayEntity) {

        new PaymentHelper().startAliPayV2(this, alipayEntity);
    }

    @Override
    public void wxPayCreateOrderFinish(WxChatPayEntity wxChatPayEntity) {
        new PaymentHelper().startWeChatPay(this, wxChatPayEntity);
    }

    @Override
    public void walletPayCreateOrderFinish(int payResult) {
        payResult(payResult, orderId);
    }

    @Override
    public void cancelPayOrderFinish(Boolean flag) {

    }

    /**
     * 支付结果从后台拿到后关闭页面
     *
     * @param payResult 支付结果  1网络异常 0成功 -1支付出错 -2用户取消支付-3支付中
     */
    @Override
    public void payResult(int payResult, String payNo) {
        if (onPayResultListener != null) onPayResultListener.onResult(payResult, payNo);
    }

    @Override
    public void showBalance(BigDecimal balance) {
        setBalance(balance);
    }

    /**
     * 支付操作后请求后端结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestPayResult(RequestPayResultEventBus bus) {
        if (TextUtils.isEmpty(bus.orderId)) {
            bus.orderId = orderId;
        }
        if (bus.payResult == 0 || bus.payResult == -3) {//付款成功或支付确认中刷新订单列表
            payResult(bus.payResult, bus.orderId);
//            payPresenter.getPayResult(bus.orderId, bus.payType, bus.payResult);
        } else if (bus.payResult == -2) {//用户取消
            payResult(-2, bus.orderId);
        }
    }

    @Override
    public void showLoadingView() {
        if (loadViewHelper == null) loadViewHelper = new LoadViewHelper();
        loadViewHelper.showProgressDialog(getViewContext());
    }

    @Override
    public void dismissLoadingView() {
        if (loadViewHelper != null) {
            loadViewHelper.dismissProgressDialog();
        }
    }

    /**
     * 支付方式选择监听器
     */
    public interface OnItemOnClickListener {
        void OnItemClicked(View view, int position);
    }

    /**
     * 支付结果监听器
     */
    public interface OnPayResultListener {
        void onResult(int payResult, String payNo);
    }

    /**
     * 确认支付
     */
    public interface OnConfirmPayListener {
        void onConfirm(String orderId);
    }

    /**
     * 取消支付
     */
    public interface OnCancelPayListener {
        void onCancel(String orderId);
    }
}
