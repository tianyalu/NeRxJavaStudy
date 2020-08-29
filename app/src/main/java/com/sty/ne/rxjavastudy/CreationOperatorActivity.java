package com.sty.ne.rxjavastudy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 创建型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class CreationOperatorActivity extends AppCompatActivity {
    private static final String TAG = CreationOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_operator);
    }

    /**
     * create 操作符创建 Observable
     * @param view
     */
    public void r01(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
            }
        }).subscribe( //订阅
                //下游
                new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext ：" + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * just 操作符创建 Observable
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable.just("A", "B") //内部会按顺序去发射 A B
                .subscribe( //订阅
                        new Observer<String>() { //下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游 onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * fromArray 操作符创建 Observable
     * @param view
     */
    public void r03(View view) {
        String[] strings = {"1", "2", "3"};
        //上游
        Observable.fromArray(strings) //内部会安装数组的顺序依次发射
                .subscribe( //订阅
                        new Observer<String>() { //下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游 onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        // Consumer 是 Observer 的简化版
        //上游
        Observable.fromArray(strings) //内部会安装数组的顺序依次发射
                .subscribe( //订阅
                        new Consumer<String>() { //下游
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "下游 accept: " + s);
                    }
                });
    }

    /**
     * 为什么只支持Object？
     * 上游没有发射有值的事件，下游无法确定类型，默认Object，RxJava泛型默认类型 == Object
     *
     * 使用场景：
     *      1.做一个耗时操作，但不需要任何数据刷新UI
     * @param view
     */
    public void r04(View view) {
        //上游无法指定事件类型
        Observable.empty() //内部一定会只调用发射onComplete()完毕事件
                .subscribe( //订阅
                        new Observer<Object>() { //下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                //没有事件可以接收
                Log.d(TAG, "onNext: " + o);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
                //隐藏加载框...
            }
        });

        //简化版的观察者
        Observable.empty().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                //接收不到的
                //没有事件可以接收
                Log.d(TAG, "onNext: " + o);
            }
        });
    }

    /**
     * range 操作符创建 Observable
     * @param view
     */
    public void r05(View view) {
        //上游
//        Observable.range(1, 8) //range内部会去发射  1 2 3 4 5 6 7 8 从1开始加 数量共8个
        Observable.range(80, 5) //range内部会去发射  80 81 82 83 84 从80开始加 数量共5个
                .subscribe( //订阅
                        new Consumer<Integer>() { //下游
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "onNext: " + integer);
            }
        });

    }
}
