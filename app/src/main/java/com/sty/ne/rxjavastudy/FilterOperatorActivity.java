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
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 过滤型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class FilterOperatorActivity extends AppCompatActivity {
    private static final String TAG = FilterOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_operator);
    }

    /**
     * filter 过滤操作符 --> 过滤不符合要求的发射事件
     * 需求：过滤掉那些不合格的奶粉，输出那些合格的奶粉
     * @param view
     */
    public void r01(View view) {
        //上游
        Observable
                .just("三鹿", "合生元", "飞鹤")
                //过滤操作符
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        //return true; //不去过滤，默认全部都会打印
                        //return false; //如果false，就全部过滤，全部都不会打印
                        if("三鹿".equals(s)) {
                            return false; //不合格，过滤掉
                        }
                        return true;
                    }
                })
                //订阅
                .subscribe(new Consumer<String>() { //下游
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });
    }

    /**
     * take 过滤操作符 --> 停止定时器
     * 定时器运行，只有在定时器运行基础上加上take过滤操作符，才能体现其价值
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable.interval(2, TimeUnit.SECONDS)
                //增加过滤操作符，停止定时器
                .take(8) //执行次数达到8停止下来
                //订阅
                .subscribe(new Consumer<Long>() { //下游
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "accept: " + aLong);
                        // 0 1 2 3 4 5 6 7
                    }
                });
    }

    /**
     * distinct 过滤操作符 --> 过滤重复发射的事件
     * @param view
     */
    public void r03(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                        e.onNext(4);
                        e.onNext(4);
                        e.onComplete();
                    }
                })
                .distinct() //过滤重复发射的事件
                //订阅
                .subscribe(new Consumer<Integer>() { //下游 观察者
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + integer);
                        // 1 2 3 4
                    }
                });
    }

    /**
     * elementAt 过滤操作符 --> 指定发射事件内容
     * @param view
     */
    public void r04(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("九阴真经");
                        e.onNext("九阳真经");
                        e.onNext("易筋经");
                        e.onNext("神照经");
                        e.onComplete();
                    }
                })
                .elementAt(100, "默认经") //指定下标输出事件
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });
    }
}
