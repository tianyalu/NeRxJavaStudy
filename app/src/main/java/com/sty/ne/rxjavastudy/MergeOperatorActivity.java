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
 * 合并型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class MergeOperatorActivity extends AppCompatActivity {
    private static final String TAG = MergeOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_operator);
    }

    /**
     * startWith 合并操作符 --> 被观察者1.startWith(被观察者2) 先执行被观察者2中发射的事件，然后执行被观察者1发射的事件
     * @param view
     */
    public void r01(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).startWith(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1000);
                e.onNext(2000);
                e.onNext(3000);
                e.onComplete();
            }
        })).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
                // D/MergeOperatorActivity: accept: 1000
                // D/MergeOperatorActivity: accept: 2000
                // D/MergeOperatorActivity: accept: 3000
                // D/MergeOperatorActivity: accept: 1
                // D/MergeOperatorActivity: accept: 2
                // D/MergeOperatorActivity: accept: 3
            }
        });
    }

    /**
     * concatWith 过滤操作符 --> 和startWith是相反的
     * 被观察者1.concatWith(被观察者2) 先执行被观察者1中发射的事件，然后执行被观察者2发射的事件
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).concatWith(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1000);
                e.onNext(2000);
                e.onNext(3000);
                e.onComplete();
            }
        })).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
                // D/MergeOperatorActivity: accept: 1
                // D/MergeOperatorActivity: accept: 2
                // D/MergeOperatorActivity: accept: 3
                // D/MergeOperatorActivity: accept: 1000
                // D/MergeOperatorActivity: accept: 2000
                // D/MergeOperatorActivity: accept: 3000
            }
        });
    }

    /**
     * concat 的特性：最多能够合并4个被观察者 --> 按照我们存入的顺序执行
     * @param view
     */
    public void r03(View view) {
        //上游 被观察者
        Observable.concat(
                Observable.just("1"),
                Observable.just("2"),
                Observable.just("3"),
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("4");
                        e.onComplete();
                    }
                })
        ).subscribe(new Consumer<String>() { //下游 观察者
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
                // D/MergeOperatorActivity: accept: 1
                // D/MergeOperatorActivity: accept: 2
                // D/MergeOperatorActivity: accept: 3
                // D/MergeOperatorActivity: accept: 4
            }
        });
    }

    /**
     * merge 合并操作符 --> 最多能够合并四个被观察者，并列（并发）执行
     * @param view
     */
    public void r04(View view) {
        //上游
//        Observable
//                //start:开始累计值 count:累计数量 initialDelay:开始时延时执行时间 period:开始后每隔多少时间执行一次
//                .intervalRange(0, 5, 1, 2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                Log.d(TAG, "accept: " + aLong);
//                // D/MergeOperatorActivity: accept: 0
//                // D/MergeOperatorActivity: accept: 1
//                // D/MergeOperatorActivity: accept: 2
//                // D/MergeOperatorActivity: accept: 3
//                // D/MergeOperatorActivity: accept: 4
//            }
//        });

        Observable observable1 = Observable.intervalRange(0, 5, 1, 2, TimeUnit.SECONDS); //0 1 2 3 4
        Observable observable2 = Observable.intervalRange(5, 5, 1, 2, TimeUnit.SECONDS); //5 6 7 8 9
        Observable observable3 = Observable.intervalRange(10, 5, 1, 2, TimeUnit.SECONDS); //10 11 12 13 14

        //上游
        Observable.merge(observable1, observable2, observable3)  //合并成一个被观察者
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.d(TAG, "accept: " + o);
                        // D/MergeOperatorActivity: accept: 0
                        // D/MergeOperatorActivity: accept: 5
                        // D/MergeOperatorActivity: accept: 10
                        // D/MergeOperatorActivity: accept: 1
                        // D/MergeOperatorActivity: accept: 6
                        // D/MergeOperatorActivity: accept: 11
                        // D/MergeOperatorActivity: accept: 2
                        // D/MergeOperatorActivity: accept: 7
                        // D/MergeOperatorActivity: accept: 12
                        // D/MergeOperatorActivity: accept: 3
                        // D/MergeOperatorActivity: accept: 8
                        // D/MergeOperatorActivity: accept: 13
                        // D/MergeOperatorActivity: accept: 4
                        // D/MergeOperatorActivity: accept: 9
                        // D/MergeOperatorActivity: accept: 14
                    }
                });
    }

    /**
     * zip 合并操作符 --> 合并的被观察者发射的事件需要对应，否则会被忽略掉
     * 需求： 考试 课程 == 分数
     * @param view
     */
    public void r05(View view) {
        //被观察者 课程
        Observable observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("英语");
                e.onNext("数学");
                e.onNext("政治");
                e.onNext("物理"); //被忽略掉
                e.onComplete();
            }
        });

        //被观察者 分数
        Observable observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(85);
                e.onNext(90);
                e.onNext(96);
                e.onComplete();
            }
        });

        Observable
                .zip(observable1, observable2, new BiFunction<String, Integer, StringBuffer>() {
                    @Override
                    public StringBuffer apply(String s, Integer integer) throws Exception {
                        return new StringBuffer().append("课程").append(s).append("==").append(integer.toString());
                    }
                })
//                .subscribe(new Consumer() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        Log.d(TAG, "最终考试的结果 accept: " + o);
//                        // D/MergeOperatorActivity: 最终考试的结果 accept: 课程英语==85
//                        // D/MergeOperatorActivity: 最终考试的结果 accept: 课程数学==90
//                        // D/MergeOperatorActivity: 最终考试的结果 accept: 课程政治==96
//                    }
//                });
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: 准备进入考场，考试了...");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "onNext: 考试结果输出 " + o);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: 考试全部完毕");
                    }
                    // onSubscribe: 准备进入考场，考试了...
                    // onNext: 考试结果输出 课程英语==85
                    // onNext: 考试结果输出 课程数学==90
                    // onNext: 考试结果输出 课程政治==96
                    // onComplete: 考试全部完毕
                });
    }
}
