package com.sty.ne.rxjavastudy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 背压模式
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class FlowableActivity extends AppCompatActivity {
    private static final String TAG = FlowableActivity.class.getSimpleName();
    private Subscription subscription;
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowable);
    }

    /**
     * @param view
     */
    public void r01(View view) {
        //上游
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        //1. 上游发射大量事件
                        //for (int i = 0; i < 129; i++) {
                        //for (int i = 0; i < Integer.MAX_VALUE; i++) {  //即使用BackpressureStrategy.BUFFER模式处理如此大量的数据依然会报异常的
                        for (int i = 0; i < 100000; i++) {
                            e.onNext(i);
                        }
                        e.onComplete();
                    }
                },
                        //缓存池 max 128
                //BackpressureStrategy.ERROR //上游发射大量事件，下游阻塞处理不过来时，放入缓存池，如果池满了，抛出异常
                BackpressureStrategy.BUFFER //上游发射大量事件，下游阻塞处理不过来时，放入缓存池，“等待”下游来接收事件
                //BackpressureStrategy.DROP //上游发射大量事件，下游阻塞处理不过来时，放入缓存池，如果池满了，就会把后面发射的事件丢弃掉
                //BackpressureStrategy.LATEST //上游发射大量事件，下游阻塞处理不过来时，只存储128个事件
                )
                .subscribeOn(Schedulers.io()) //给上游分配线程
                .observeOn(AndroidSchedulers.mainThread()) //给下游分配线程
                .subscribe(new Subscriber<Integer>() { //完整版本的下游
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        //如果是同步的，不执行s.request() 会抛出异常，外界再调用subscription.request(1) 无效果
                        //如果是异步的，不执行s.request()不会抛出异常，因为上游一直在发射事件，不会等待下游的，此时外界再调用subscription.request(1)是可以的
                        //s.request(5); //只请求输出5次，给下游打印
                        //s.request(129); //只请求输出129次，给下游打印
                        //s.request(Integer.MAX_VALUE); //只请求Integer.MAX_VALUE次，给下游打印
                    }

                    @Override
                    public void onNext(Integer integer) {
                        //2. 模拟下游阻塞，处理不过来
                        try {
                            Thread.currentThread().sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //下游一旦处理了一个事件，缓存池中的事件数量-1
                        Log.d(TAG, "onNext: " + integer);
                        // D/FlowableActivity: onNext: 0
                        // D/FlowableActivity: onNext: 1
                        // D/FlowableActivity: onNext: 2
                        // D/FlowableActivity: onNext: 3
                        // D/FlowableActivity: onNext: 4
                        // D/FlowableActivity: onError: create: could not emit value due to lack of requests

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError: " + t.getMessage());
                        //上游还有剩余的事件无法被处理，因为没有去请求
                        //onError: create: could not emit value due to lack of requests
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 手动点击处理上游发射的事件：subscription.request(1)  异步才有效果
     * @param view
     */
    public void r02(View view) {
        //如果是同步的话需要等待下游处理后然后再发射后面的事件，因为等待下游，没有request，所以抛出异常：
        // create: could not emit value due to lack of requests，所以打印不出来（点击r02再请求没有效果）

        //如果是异步的话，上游会不停地发射，则可以打印出来
        if(subscription != null) {
            subscription.request(10); //点击一下就接收十个，取出来给下游处理
        }
    }

    /**
     * fromArray的区别
     * @param view
     */
    public void r03(View view) {
        String[] strings = {"1", "2", "3"};
        //for
        for (String string : strings) {
            Log.d(TAG, "for: " + string);
        }

        //Observable
        Observable.fromArray(strings)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "Observable accept: " + s);
                    }
                });

        //Flowable
        Flowable.fromArray(strings)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "Flowable accept: " + s);
                    }
                });
    }

    /**
     * just的区别
     * @param view
     */
    public void r04(View view) {
        //Observable -- Observer
        Observable.just("张三", "李四", "王五")
                .subscribe(new Observer<String>() { //下游 Observer完整版
                    @Override
                    public void onSubscribe(Disposable d) {
                        //d.dispose();  //可以中断-->切断下游（上游还在发射事件，只是下游不再接收事件）
                        disposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        //Flowable -- Subscriber
        Flowable.just("张三", "李四", "王五")
                .subscribe(new Subscriber<String>() { //下游Observer完整版
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1); //取出来给下游接收
                    }

                    @Override
                    public void onNext(String s) {
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //上游还在执行耗时操作，Activity被关闭， onDestroy 切断下游，不再接收上游事件
        if(disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * map的区别
     * @param view
     */
    public void r05(View view) {
        //Observable 上游
        Observable.just("url")
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return null;
                    }
                })
                .flatMap(new Function<Bitmap, ObservableSource<Bitmap>>() {
                    @Override
                    public ObservableSource<Bitmap> apply(Bitmap bitmap) throws Exception {
                        //Bitmap 是伪代码
                        Bitmap bitmap1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        Bitmap bitmap3 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        return Observable.just(bitmap1, bitmap2, bitmap3); //注意这里
                    }
                })
                .subscribe(new Observer<Bitmap>() { //下游
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        //Flowable 上游
        Flowable.just("url")
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return null;
                    }
                })
                .flatMap(new Function<Bitmap, Publisher<Bitmap>>() {
                    @Override
                    public Publisher<Bitmap> apply(Bitmap bitmap) throws Exception {
                        //Bitmap 是伪代码
                        Bitmap bitmap1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        Bitmap bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        Bitmap bitmap3 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        return Flowable.just(bitmap1, bitmap2, bitmap3); //注意这里
                    }
                })
                .subscribe(new Subscriber<Bitmap>() { //下游
                    @Override
                    public void onSubscribe(Subscription s) {
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * create的区别
     * @param view
     */
    public void r06(View view) {
        //Observable上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("test");
                e.onComplete();
            }
        }).subscribe(new Consumer<String>() { //下游简化版
            @Override
            public void accept(String s) throws Exception {

            }
        });

        //Flowable上游
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("test");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribe(new Consumer<String>() { //简化版下游
            @Override
            public void accept(String s) throws Exception {

            }
        });
    }
}
