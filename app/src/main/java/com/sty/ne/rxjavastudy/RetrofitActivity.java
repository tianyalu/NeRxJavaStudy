package com.sty.ne.rxjavastudy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sty.ne.rxjavastudy.retrofit_okhttp.IRequestNetwork;
import com.sty.ne.rxjavastudy.retrofit_okhttp.LoginRequest;
import com.sty.ne.rxjavastudy.retrofit_okhttp.LoginResponse;
import com.sty.ne.rxjavastudy.retrofit_okhttp.MyRetrofit;
import com.sty.ne.rxjavastudy.retrofit_okhttp.RegisterRequest;
import com.sty.ne.rxjavastudy.retrofit_okhttp.RegisterResponse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Retrofit + RxJava
 * 需求：
 * 1. 请求服务器注册操作
 * 2. 注册完成之后更新UI
 * 3. 马上去登录服务器操作
 * 4. 登录完成之后更新登录的UI
 * @Author: tian
 * @UpdateDate: 2020/9/1 2:38 PM
 */
public class RetrofitActivity extends AppCompatActivity {
    private static final String TAG = RetrofitActivity.class.getSimpleName();
    private Button btnRequestNetwork;
    private TextView tvRegisterUi;
    private TextView tvLoginUi;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        initView();
    }

    private void initView() {
        btnRequestNetwork = findViewById(R.id.btn_request_network);
        tvRegisterUi = findViewById(R.id.tv_register_ui);
        tvLoginUi = findViewById(R.id.tv_login_ui);
    }

    /**
     * 分开写实现需求
     */
    private void requestNetwork() {
        MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                //1. 请求服务器注册操作
                //IRequestNetwork.loginAction
                .registerAction(new RegisterRequest()) //Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io())  //给上游分配异步线程
                .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
                //2. 注册完成后更新注册UI
                .subscribe(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        //更新注册相关的所有UI
                        //todo
                    }
                });

        MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                //3. 马上去登录服务器操作
                //IRequestNetwork.loginAction
                .loginAction(new LoginRequest()) //Observable<LoginResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io())  //给上游分配异步线程
                .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
                //4. 登录完成之后更新登录的UI
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        //更新登录相关的所有UI
                        //todo
                    }
                });

    }

    /**
     * 一行代码实现需求
     */
    private void requestNetwork2() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在请求中...");

        MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                //1. 请求服务器注册操作 //TODO 第二步
                //IRequestNetwork.loginAction
                .registerAction(new RegisterRequest()) //Observable<RegisterResponse> 上游 被观察者 耗时操作
                .subscribeOn(Schedulers.io())  //指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer（中途可以用observerOn切换线程），它可以在流中的任何位置，如果有多个subscribeOn,只有第一个生效
                .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
                //2. 注册完成后更新注册UI
                .doOnNext(new Consumer<RegisterResponse>() { //每次在Observer的onNext方法调用之前被调用，但是调用顺序和其在流中的位置顺序一致
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        //更新注册相关的所有UI //TODO 第三步
                        tvRegisterUi.setText("注册成功");
                    }
                })
                //3. 马上去登录服务器操作
                .observeOn(Schedulers.io()) //给下游切换子线程
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        //这里还可以拿到注册后的响应对象RegisterResponse
                        //执行登录服务器操作 //TODO 第四步
                        Observable<LoginResponse> observable = MyRetrofit.createRetrofit().create(IRequestNetwork.class)
                                .loginAction(new LoginRequest());
                        return observable;
                    }
                })
                //4. 登录完成之后更新登录的UI
                .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //TODO 第一步
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        //更新登录相关的所有UI //TODO 第五步
                        tvLoginUi.setText("登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        //TODO 第六步
                        if(progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
