package com.zhangteng.payutil.http.response;

import android.text.TextUtils;

import com.zhangteng.base.base.BaseApplication;
import com.zhangteng.base.mvp.base.BaseHttpEntity;
import com.zhangteng.base.utils.NetworkUtils;
import com.zhangteng.payutil.constants.Constants;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava.HttpException;


/**
 * @ClassName: Request
 * @Description: 回调处理
 * @Author: Swing 763263311@qq.com
 * @Date: 2020/11/2 0002 下午 15:57
 */
public class Request<T extends BaseResponse> {
    private Disposable disposable;

    public Request() {
    }

    public void request(Observable<T> observable, final BaseHttpEntity<T> result) {
        result.onStart();
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<T>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Request.this.disposable = d;
                    }

                    @Override
                    public void onNext(T response) {
                        if (response.status == Constants.SUCCESS) {
                            result.onSuccess(response);
                        } else if (response.status == Constants.DEBLOCKING) {
                            result.onError(response.status, TextUtils.isEmpty(response.message) ? "您已经被封号，请联系我们。\\nservice@tongxuecool.com" : response.message);
                        } else if (response.status == Constants.PERMISSION_DENIED) {
                            String errorMsg = TextUtils.isEmpty(response.message) ? "登录失效，请重新登录" : response.message;
                            result.onError(response.status, errorMsg);
                        } else {
                            result.onError(response.status, response.message);
                        }
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetworkUtils.isAvailable(BaseApplication.getInstance())) {
                            result.onNoNetworkError();
                            result.onFinish();
                            return;
                        }
                        if (e instanceof HttpException) {
                            result.onHttpError(((HttpException) e).code(), ((HttpException) e).message());
                        } else if (e.toString().contains("com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT")) {
                            result.onError(Constants.SERVER_ERROR, e.toString());
                        } else {
                            result.onError(Constants.SERVER_ERROR, e.toString());
                        }
                        result.onFinish();
                    }

                    @Override
                    public void onComplete() {
                        result.onFinish();
                    }
                });
    }
}
