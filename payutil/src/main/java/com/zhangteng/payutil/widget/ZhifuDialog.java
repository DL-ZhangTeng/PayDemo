package com.zhangteng.payutil.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhangteng.base.base.BaseDialog;
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

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 支付对话框
 * 已实现支付业务，生成订单id后对ZhifuDialog设置orderId即可走支付流程，设置回调OnPayResultListener获取支付结果（生成订单id与取消订单的业务未实现）
 * ZhifuDialog zhifuDialog = new ZhifuDialog(this);
 * zhifuDialog.setOnCancelPayListener(orderId -> {
 * presenter.deleteOrder(goodsOrder.getId());
 * if (zhifuDialog.isShowing()) zhifuDialog.dismiss();
 * });
 * zhifuDialog.setOnConfirmPayListener(orderId -> {
 * //处理业务已实现
 * });
 * zhifuDialog.setOnPayResultListener((payResult, payNo) -> {
 * if (payResult == 0 || payResult == -3) {//付款成功或支付确认中刷新订单列表
 * List<PaySuccessEventBus.CouponsOrderBus> ids = new ArrayList<>();
 * for (CouponsOrder couponsOrder : storeMuluseCoupons.getSelectedCouponsOrder()) {
 * ids.add(couponsOrder.toCouponsOrderBus());
 * }
 * EventBus.getDefault().post(new PaySuccessEventBus(ids));
 * } else if (payResult == -2) {//取消订单
 * presenter.deleteOrder(orderId);
 * }
 * payResult(payResult, payNo);
 * });
 * if (!zhifuDialog.isShowing()) zhifuDialog.show();
 *
 * @author Swing
 * @date 2019-06-20
 */
public class ZhifuDialog extends BaseDialog implements PayView {
    private RadioButton walletDialogWalletRb;
    private TextView walletDialogTishiTv;
    private RadioButton walletDialogAliRb;
    private RadioButton walletDialogWxRb;
    private TextView walletDialogMoneyTv;

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
     * 各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    private int typeName;
    private PayPresenter payPresenter;
    private OnItemOnClickListener onItemOnClickListener;
    private OnPayResultListener onPayResultListener;
    private OnConfirmPayListener onConfirmPayListener;
    private OnCancelPayListener onCancelPayListener;

    private WeakReference<Activity> activity;

    private LoadViewHelper loadViewHelper;

    public ZhifuDialog(@NonNull Activity context) {
        super(context);
        this.activity = new WeakReference<>(context);
    }

    public ZhifuDialog(@NonNull Activity context, BigDecimal balance, float amount) {
        super(context);
        this.activity = new WeakReference<>(context);
        setBalance(balance);
        setPaymentAmount(amount);
    }

    @Override
    public int getSelfTitleView() {
        return 0;
    }

    @Override
    public int getSelfContentView() {
        return R.layout.wallet_dialog_zhifu_content;
    }

    @Override
    public int getSelfButtonView() {
        return R.layout.wallet_dialog_zhifu_button;
    }

    @Override
    public void initView(View view) {
        walletDialogWalletRb = view.findViewById(R.id.wallet_dialog_wallet_rb);
        walletDialogTishiTv = view.findViewById(R.id.wallet_dialog_tishi_tv);
        walletDialogAliRb = view.findViewById(R.id.wallet_dialog_ali_rb);
        walletDialogWxRb = view.findViewById(R.id.wallet_dialog_wx_rb);
        walletDialogMoneyTv = view.findViewById(R.id.wallet_dialog_money_tv);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        payPresenter = new PayPresenter();
        payPresenter.attachView(this);
        setOnCancelClickListener(view1 -> {
            if (onCancelPayListener != null) onCancelPayListener.onCancel(orderId);
            if (!TextUtils.isEmpty(orderId)) {
                cancelPayOrder(orderId);
            }
            if (isShowing()) dismiss();
        });
        setOnConfirmClickListener(view1 -> {
            if (!TextUtils.isEmpty(orderId))
                createPayOrder(orderId);
            if (onConfirmPayListener != null) onConfirmPayListener.onConfirm(orderId);
        });
        walletDialogWalletRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogAliRb.setChecked(false);
                walletDialogWxRb.setChecked(false);
                if (onItemOnClickListener != null) onItemOnClickListener.OnItemClicked(view, 0);
            }
        });
        walletDialogAliRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogWalletRb.setChecked(false);
                walletDialogWxRb.setChecked(false);
                if (onItemOnClickListener != null) onItemOnClickListener.OnItemClicked(view, 1);
            }
        });
        walletDialogWxRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogWalletRb.setChecked(false);
                walletDialogAliRb.setChecked(false);
                if (onItemOnClickListener != null) onItemOnClickListener.OnItemClicked(view, 2);
            }
        });
        //获取钱包余额
//        payPresenter.getBalance();
    }

    @Override
    public void show() {
        super.show();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 订单id
     */
    public ZhifuDialog setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * 各个模块名【1补贴 2悬赏 3笔记 4秀场】
     */
    public ZhifuDialog setTypeName(int typeName) {
        this.typeName = typeName;
        return this;
    }

    /**
     * 设置应支付金额
     *
     * @param amount 支付金额
     */
    public ZhifuDialog setPaymentAmount(float amount) {
        this.amount = amount;
        initTishi();
        walletDialogMoneyTv.setText(new DecimalFormat("0.00").format(amount));
        return this;
    }

    /**
     * 设置余额
     *
     * @param balance 余额
     */
    public ZhifuDialog setBalance(BigDecimal balance) {
        this.balance = balance;
        initTishi();
        walletDialogWalletRb.setText(String.format("钱包(剩余%s元)", balance.toString()));
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

    public ZhifuDialog setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
        return this;
    }

    public ZhifuDialog setOnPayResultListener(OnPayResultListener onPayResultListener) {
        this.onPayResultListener = onPayResultListener;
        return this;
    }

    public ZhifuDialog setOnConfirmPayListener(OnConfirmPayListener onConfirmPayListener) {
        this.onConfirmPayListener = onConfirmPayListener;
        return this;
    }

    public ZhifuDialog setOnCancelPayListener(OnCancelPayListener onCancelPayListener) {
        this.onCancelPayListener = onCancelPayListener;
        return this;
    }

    @Override
    public Context getViewContext() {
        return getContext();
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

    /**
     * 暂时没有取消订单接口
     */
    @Override
    public void cancelPayOrder(String orderId) {

    }

    @Override
    public void aliPayCreateOrderFinish(AlipayEntity alipayEntity) {
        if (activity.get() != null)
            new PaymentHelper().startAliPayV2(activity.get(), alipayEntity);
    }

    @Override
    public void wxPayCreateOrderFinish(WxChatPayEntity wxChatPayEntity) {
        if (activity.get() != null)
            new PaymentHelper().startWeChatPay(activity.get(), wxChatPayEntity);
    }

    @Override
    public void walletPayCreateOrderFinish(int flag) {
        payResult(flag, orderId);
    }

    /**
     * 支付结果从后台拿到后关闭页面
     *
     * @param payResult 支付结果  1网络异常 0成功 -1支付出错 -2用户取消支付-3支付中
     */
    @Override
    public void payResult(int payResult, String payNo) {
        if (onPayResultListener != null) onPayResultListener.onResult(payResult, payNo);
        if (isShowing()) dismiss();
    }

    @Override
    public void showbalance(BigDecimal balance) {
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
