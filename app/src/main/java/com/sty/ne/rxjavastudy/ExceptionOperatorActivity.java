package com.sty.ne.rxjavastudy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 异常处理型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class ExceptionOperatorActivity extends AppCompatActivity {
    private static final String TAG = ExceptionOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_operator);
    }

    /**
     * onErrorReturn 异常处理操作符
     * 1. 能够接收e.onError
     * 2. 如果接收到异常，会中断上游后续发射的所有事件
     * 3. 可以返回标识 400
     * 4. 用这个操作符后下游就不走onError而走onNext了，然后会走onComplete；不用的话就走下游的onError，之后不会走onComplete
     * @param view
     */
    public void r01(View view) {
        //上游 被观察者
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        for (int i = 0; i < 100; i++) {
                            if(i == 5) {
                                //RxJava中是不标准的用法，用不用onErrorReturn操作符都会崩溃
//                                throw new IllegalAccessError("我要报错了");

                                //RxJava中标准的操作
                                e.onError(new IllegalAccessError("我要报错了"));
                            }
                            e.onNext(i);
                        }
                        e.onComplete();
                    }
                })
                //在上游和下游之间添加异常操作符
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        //处理、记录异常，然后通知给下一层
                        Log.d(TAG, "onErrorReturn: " + throwable.getMessage());
                        return 400;
                    }
                })
                .subscribe(new Observer<Integer>() { //完整版下游观察者
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                        //用onErrorReturn操作符的话
                        // D/ExceptionOperatorActivity: onNext: 0
                        // D/ExceptionOperatorActivity: onNext: 1
                        // D/ExceptionOperatorActivity: onNext: 2
                        // D/ExceptionOperatorActivity: onNext: 3
                        // D/ExceptionOperatorActivity: onNext: 4
                        // D/ExceptionOperatorActivity: onErrorReturn: 我要报错了
                        // D/ExceptionOperatorActivity: onNext: 400
                        // D/ExceptionOperatorActivity: onComplete:
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        //不用onErrorReturn操作符的话
                        // D/ExceptionOperatorActivity: onNext: 0
                        // D/ExceptionOperatorActivity: onNext: 1
                        // D/ExceptionOperatorActivity: onNext: 2
                        // D/ExceptionOperatorActivity: onNext: 3
                        // D/ExceptionOperatorActivity: onNext: 4
                        // D/ExceptionOperatorActivity: onError: 我要报错了
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * onErrorResumeNext 异常处理操作符
     * 1. 能够接收e.onError
     * 2. 如果接收到异常，会中断上游后续发射的所有事件
     * 3. 可以返回被观察者（被观察者可以再次发射多次事件给下游）
     * 4. 用这个操作符后下游就不走onError而走onNext了，然后会走onComplete
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if(i == 5) {
                        //RxJava中是不标准的用法，用不用onErrorReturn操作符都会崩溃
//                        throw new IllegalAccessError("我要报错了");
                        e.onError(new IllegalAccessError("我要报错了"));
                    }else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                //onErrorResumeNext 返回的是被观察者，所以可以再多次发射给下游，被观察者接收
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(400);
                        e.onNext(400);
                        e.onNext(400);
                        e.onComplete();
                    }
                });
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
                // D/ExceptionOperatorActivity: onNext: 0
                // D/ExceptionOperatorActivity: onNext: 1
                // D/ExceptionOperatorActivity: onNext: 2
                // D/ExceptionOperatorActivity: onNext: 3
                // D/ExceptionOperatorActivity: onNext: 4
                // D/ExceptionOperatorActivity: onNext: 400
                // I/chatty: uid=10076(com.sty.ne.rxjavastudy) identical 1 line
                // D/ExceptionOperatorActivity: onNext: 400
                // D/ExceptionOperatorActivity: onComplete:
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    /**
     * onExceptionResumeNext 异常处理操作符
     * 能在发生异常的时候扭转乾坤（这个异常一定是可以接受的[不严重的异常]，才这样使用）
     * 慎用：自己考虑要不要使用
     * @param view
     */
    public void r03(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        for (int i = 0; i < 100; i++) {
                            if(i == 5) {
//                                throw new IllegalAccessException("错了"); //用不用onExceptionResumeNext操作符都不会崩溃
                                e.onError(new IllegalAccessException("错了"));
                            }else {
                                e.onNext(i);
                            }
                        }
                        e.onComplete();  //一定要最后执行
                    }
                })
                //在上游和下游中间增加异常操作符
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        observer.onNext(404);
                    }
                })
                //订阅
                .subscribe(new Observer<Integer>() { //下游
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                        // D/ExceptionOperatorActivity: onNext: 0
                        // D/ExceptionOperatorActivity: onNext: 1
                        // D/ExceptionOperatorActivity: onNext: 2
                        // D/ExceptionOperatorActivity: onNext: 3
                        // D/ExceptionOperatorActivity: onNext: 4
                        // D/ExceptionOperatorActivity: onNext: 404
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete:");
                    }
                });
    }

    /**
     * retry 异常处理操作符
     * @param view
     */
    public void r04(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        for (int i = 0; i < 100; i++) {
                            if(i == 5) {
                                e.onError(new IllegalAccessException("出错了"));
                            }else {
                                e.onNext(i);
                            }
                        }
                        e.onComplete();
                    }
                })
                //演示一(无限重试)
//                .retry(new Predicate<Throwable>() {
//                    @Override
//                    public boolean test(Throwable throwable) throws Exception {
//                        Log.d(TAG, "test: " + throwable.getMessage());
//                        //return false; //代表不去重试
//                        return true; //代表一直重试
//                    }
//                })
                //演示二(重试3次[共执行4次])
                .retry(3, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d(TAG, "test: " + throwable.getMessage());
                        return true;
                    }
                })
                //演示三(打印重试了多少次,计数[无限重试])
//                .retry(new BiPredicate<Integer, Throwable>() {
//                    @Override
//                    public boolean test(Integer integer, Throwable throwable) throws Exception {
//                        Thread.sleep(2);
//                        Log.d(TAG, "retry: 已经重试了：" + integer + "次 e:" + throwable.getMessage());
//                        return true; //重试
//                    }
//                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                        // D/ExceptionOperatorActivity: onNext: 0
                        // D/ExceptionOperatorActivity: onNext: 1
                        // D/ExceptionOperatorActivity: onNext: 2
                        // D/ExceptionOperatorActivity: onNext: 3
                        // D/ExceptionOperatorActivity: onNext: 4
                        // D/ExceptionOperatorActivity: test: 出错了
                        // D/ExceptionOperatorActivity: onError: 出错了
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
}
