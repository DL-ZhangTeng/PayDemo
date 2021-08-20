package com.zhangteng.payutil.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
 * 支付对话框(底部弹出)
 * 已实现支付业务，生成订单id后对BottomPayDialog设置orderId即可走支付流程，设置回调OnPayResultListener获取支付结果（生成订单id与取消订单的业务未实现）
 * BottomPayDialog zhifuDialog = new BottomPayDialog(this);
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
public class BottomPayDialog extends BaseDialog implements PayView {
    private RadioButton walletDialogWalletRb;
    private TextView walletDialogTishiTv;
    private RadioButton walletDialogAliRb;
    private RadioButton walletDialogWxRb;
    private TextView walletDialogMoneyTv;
    private TextView walletDialogOrderInfo;
    private TextView walletDialogTotalValue;
    private TextView walletDialogConfirm;
    private ImageView walletDialogPayClose;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 支付内容
     */
    private String content;
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

    private WeakReference<Activity> activity;

    private LoadViewHelper loadViewHelper;

    public BottomPayDialog(@NonNull Activity context) {
        super(context, R.style.SelfDialog);
        this.activity = new WeakReference<>(context);
    }

    public BottomPayDialog(@NonNull Activity context, float amount) {
        super(context, R.style.SelfDialog);
        this.activity = new WeakReference<>(context);
        setPaymentAmount(amount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.showAsDropUp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
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
        walletDialogOrderInfo = view.findViewById(R.id.wallet_dialog_order_info);
        walletDialogTotalValue = view.findViewById(R.id.wallet_dialog_totalValue);
        walletDialogConfirm = view.findViewById(R.id.wallet_dialog_confirm);
        walletDialogPayClose = view.findViewById(R.id.wallet_dialog_pay_close);

        payPresenter = new PayPresenter();
        payPresenter.attachView(this);
        payPresenter.onStart();
        walletDialogPayClose.setOnClickListener(v -> {
            if (onCancelPayListener != null) onCancelPayListener.onCancel(orderId);
            if (!TextUtils.isEmpty(orderId)) {
                cancelPayOrder(orderId);
            }
            if (isShowing()) dismiss();
        });
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
        walletDialogAliRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogWxRb.setChecked(false);
                if (onItemOnClickListener != null) onItemOnClickListener.OnItemClicked(view, 1);
            }
        });
        walletDialogWxRb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                walletDialogAliRb.setChecked(false);
                if (onItemOnClickListener != null) onItemOnClickListener.OnItemClicked(view, 2);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        EventBus.getDefault().register(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
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
    public BottomPayDialog setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * 各个模块名
     */
    public BottomPayDialog setTypeName(int typeName) {
        this.typeName = typeName;
        return this;
    }

    /**
     * 设置应支付金额
     *
     * @param amount 支付金额
     */
    public BottomPayDialog setPaymentAmount(float amount) {
        this.amount = amount;
        String total = new DecimalFormat("0.00").format(amount);
        walletDialogTotalValue.setText(String.format("%s元", total));
        walletDialogMoneyTv.setText(total);
        walletDialogConfirm.setText(String.format("确认支付%s元", total));
        return this;
    }

    /**
     * 设置余额
     *
     * @param balance 余额
     */
    public BottomPayDialog setBalance(BigDecimal balance) {
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

    /**
     * @param content 设置订单信息
     * @description 设置支付content
     */
    public BottomPayDialog setWalletDialogOrderInfo(String content) {
        this.content = content;
        walletDialogOrderInfo.setVisibility(View.VISIBLE);
        walletDialogOrderInfo.setText(content);
        return this;
    }

    public BottomPayDialog setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
        return this;
    }

    public BottomPayDialog setOnPayResultListener(OnPayResultListener onPayResultListener) {
        this.onPayResultListener = onPayResultListener;
        return this;
    }

    public BottomPayDialog setOnConfirmPayListener(OnConfirmPayListener onConfirmPayListener) {
        this.onConfirmPayListener = onConfirmPayListener;
        return this;
    }

    public BottomPayDialog setOnCancelPayListener(OnCancelPayListener onCancelPayListener) {
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
        if (walletDialogAliRb.isChecked()) {
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
        if (isShowing()) dismiss();
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
