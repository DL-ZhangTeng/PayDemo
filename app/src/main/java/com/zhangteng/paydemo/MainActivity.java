package com.zhangteng.paydemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangteng.payutil.widget.CenterPayDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CenterPayDialog(this)
                .setPaymentAmount(1000 / 100f)
                .setOrderId("123456...")
                .setTypeName(3)
                .setOnPayResultListener((payResult, payNo) -> {
                    if (payResult == 0) {
                        //成功
                    }
                })
                .show();
    }
}