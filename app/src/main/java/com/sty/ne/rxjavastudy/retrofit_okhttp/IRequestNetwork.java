package com.sty.ne.rxjavastudy.retrofit_okhttp;

import io.reactivex.Observable;
import retrofit2.http.Body;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/1 2:53 PM
 */
public interface IRequestNetwork {
    //请求注册功能 耗时操作
    public Observable<RegisterResponse> registerAction(@Body RegisterRequest registerRequest);

    //请求登录功能 耗时操作
    public Observable<LoginResponse> loginAction(@Body LoginRequest loginRequest);
}
