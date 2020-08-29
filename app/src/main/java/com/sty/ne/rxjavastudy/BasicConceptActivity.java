package com.sty.ne.rxjavastudy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BasicConceptActivity extends AppCompatActivity {
    private static final String TAG = BasicConceptActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_concept);
    }

    /**
     * 1、起点与终点
     * @param view
     */
    public void r01(View view) {
        //起点 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribe( // 订阅 == registerObserver
                //终点 观察者 <-- 只有一个（区别于标准的观察者模式）
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

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
     * 2、上游与下游 拆分写法
     * @param view
     */
    public void r02(View view) {
        //上游 Observable 被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            //ObservableEmitter<Integer> emitter 发射器 发射事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "上游subscribe：发射事件");
                //发送事件
                emitter.onNext(1);
                Log.d(TAG, "上游subscribe：发射完成");
            }
        });
        //下游 Observer 观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游 接收处理 onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        //被观察者（上游）订阅观察者（下游）
        observable.subscribe(observer);
    }

    /**
     * 2、上游与下游 链式调用
     * @param view
     */
    public void r03(View view) {
        //上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "上游subscribe：发射事件");
                //发送事件
                emitter.onNext(1);
                Log.d(TAG, "上游subscribe：发射完成");
            }
        }).subscribe( //订阅操作
                //下游 Observer 观察者
                new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游 接收处理 onNext: " + integer);
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
     * 3、流程整理
     * @param view
     */
    public void r04(View view) {
        //上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //发射
                Log.d(TAG, "上游 subscribe: 开始发射...");  //step 2
                emitter.onNext("RxJavaStudy");
                emitter.onComplete(); //发射完毕    //step 4
                Log.d(TAG, "下游 subscribe: 发射完成...");  //step 6

            }
        }).subscribe( //订阅
                //下游 Observer 观察者
                new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //弹出加载框...
                Log.d(TAG, "上游和下游订阅成功 onSubscribe 1");  //step 1
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "下游接收 onNext：" + s); //step 3
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() { //只有接收完成之后，上游的最后log才会打印 即step6
                //隐藏加载框...
                Log.d(TAG, "下游接收完成 onComplete"); //step 5
            }
        });
    }

    /**
     * 3、流程整理2
     * @param view
     */
    public void r05(View view) {
        //上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //发射
                Log.d(TAG, "上游 subscribe: 开始发射...");  //step 2
                emitter.onNext("RxJavaStudy");
//                emitter.onComplete(); //发射完毕    //step 4
                Log.d(TAG, "下游 subscribe: 发射完成...");  //step 6

                emitter.onError(new IllegalAccessException("error rxJava"));

                //结论：在 onComplete()/onError() 发射完成之后再发射事件，此时下游不再接收上游的事件
                emitter.onNext("a");
                emitter.onNext("b");
                emitter.onNext("c");

                //结论：已经发射了onComplete()，再发射onError()，RxJava会报错，不允许；
                //结论：先发射onError()，再发射onComplete()，不会报错，但此时onComplete()不会被下游接收到了
            }
        }).subscribe( //订阅
                //下游 Observer 观察者
                new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //弹出加载框...
                        Log.d(TAG, "上游和下游订阅成功 onSubscribe 1");  //step 1
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "下游接收 onNext：" + s); //step 3
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "下游接收 onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() { //只有接收完成之后，上游的最后log才会打印 即step6
                        //隐藏加载框...
                        Log.d(TAG, "下游接收完成 onComplete"); //step 5
                    }
                });
    }


    private Disposable d;
    /**
     * 切断下游，让下游不再接收上游的事件，也就是说不会去更新UI
     * @param view
     */
    public void r06(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribe(//订阅下游
                //下游
                new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                BasicConceptActivity.this.d = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "下游接收 onNext: " + integer);

                //接收上游的一个事件之后就切断下游，让下游不再接收
                //d.dispose();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //切断下游
        if(d != null) {
            d.dispose();
        }
    }
}
