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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 条件型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class ConditionOperatorActivity extends AppCompatActivity {
    private static final String TAG = ConditionOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_operator);
    }

    /**
     * all 条件操作符（如同if那样的功能） --> 全部为true才为true，只要有一个为false，就返回false（&&）
     * 需求：只要有一个 cc 的就返回false
     * @param view
     */
    public void r01(View view) {
        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "cc";

        //平常写法
        if("cc".equals(v1) || "cc".equals(v2) || "cc".equals(v3) || "cc".equals(v4)) {
            Log.d(TAG, "r01: " + false);
        }else {
            Log.d(TAG, "r01: " + true);
        }
        //false

        //RxJava写法
        //上游
        Observable
                .just(v1, v2, v3, v4) //RxJava 2.0 之后不能传null，否则会报错
                //条件型操作符
                .all(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !"cc".equals(s);
                    }
                })
                //订阅
                .subscribe(new Consumer<Boolean>() { //下游
                    @Override
                    public void accept(Boolean b) throws Exception {
                        Log.d(TAG, "accept: " + b); // false
                    }
                });
    }

    /**
     * contains 条件操作符 --> 是否包含
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable
                .just("JavaSE", "JavaEE", "JavaME", "Android", "IOS", "Rect.js", "NDK")
                .contains("Android") //是否包含Android，条件是否满足
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        Log.d(TAG, "accept: " + b);  //true
                    }
                });
    }

    /**
     * any 条件操作符（和all相反） --> 全部为false才为false，只要有一个为true，就返回true（||）
     * @param view
     */
    public void r03(View view) {
        //上游
        Observable
                .just("JavaSE", "JavaEE", "JavaME", "Android", "IOS", "Rect.js", "NDK")
                .any(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return "Android".equals(s);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        Log.d(TAG, "accept: " + b);
                    }
                });
    }

}
