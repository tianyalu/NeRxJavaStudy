package com.sty.ne.rxjavastudy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;

/**
 * 变换型操作符
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class TransformationOperatorActivity extends AppCompatActivity {
    private static final String TAG = TransformationOperatorActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transformation_operator);
    }

    /**
     * map 变换型操作符
     * map 变换过程中有变换返回null时下游无法接收
     * @param view
     */
    public void r01(View view) {
        //上游
        Observable.just(1) //发射事件
                //在上游和下游之间变换
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        Log.d(TAG, "map1 apply ：" + integer); //1
                        return "[ " + integer + " ]";
                    }
                })
                //第二次变换
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        Log.d(TAG, "map2 apply ：" + s); //[ 1 ]
                        return Bitmap.createBitmap(1920, 1280, Bitmap.Config.ARGB_8888);

                        //return null; //如果返回null，下游无法接收
                    }
                })
                .subscribe( //订阅
                        new Observer<Bitmap>() { //下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap bitmap) {
                Log.d(TAG, "下游接收 onNext ：" + bitmap);
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
     * flatMap 变换型操作符
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable.just(111)
                //变换操作符
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final Integer integer) throws Exception {
                        //ObservableSource可以再次手动发射事件
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> e) throws Exception {
                                e.onNext(integer + " flatMap变换操作符");
                                e.onNext(integer + " flatMap变换操作符");
                                e.onNext(integer + " flatMap变换操作符");
                                //如果发射完全相同的内容会有如下打印内容（也有可能第二行的意思是内容完全相同，就省略显示1行的打印日志）
                                // D/TransformationOperatorActivity: 下游接收 变换操作符发射的事件 accept: 111 flatMap变换操作符
                                // I/chatty: uid=10076(com.sty.ne.rxjavastudy) identical 1 lines
                                // D/TransformationOperatorActivity: 下游接收 变换操作符发射的事件 accept: 111 flatMap变换操作符
                            }
                        });
                    }
                })
                //订阅
                .subscribe(
                        //下游
                        new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "下游接收 变换操作符发射的事件 accept: " + s);
            }
        });
    }

    /**
     * flatMap 变换型操作符：体现不排序
     * @param view
     */
    public void r03(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("步惊云");
                e.onNext("聂风");
                e.onNext("雄霸");
            }
        })
        //变换操作符
        .flatMap(new Function<String, ObservableSource<?>>() { //? 通配符 默认Object
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(s + " 下标：" + (i + 1) );
                }
                return Observable.fromIterable(list).delay(1, TimeUnit.SECONDS); //创建型操作符，创建被观察者
            }
        })

        //订阅
        .subscribe(new Consumer<Object>() { //下游
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "下游 accept：" + o.toString() );
               // D/TransformationOperatorActivity: 下游 accept：步惊云 下标：1
               // D/TransformationOperatorActivity: 下游 accept：雄霸 下标：1
               // D/TransformationOperatorActivity: 下游 accept：雄霸 下标：2
               // D/TransformationOperatorActivity: 下游 accept：雄霸 下标：3
               // D/TransformationOperatorActivity: 下游 accept：步惊云 下标：2
               // D/TransformationOperatorActivity: 下游 accept：聂风 下标：1
               // D/TransformationOperatorActivity: 下游 accept：步惊云 下标：3
               // D/TransformationOperatorActivity: 下游 accept：聂风 下标：2
               // D/TransformationOperatorActivity: 下游 accept：聂风 下标：3
            }
        });
    }

    /**
     * concatMap 变换型操作符：具有排序特性
     * @param view
     */
    public void r04(View view) {
        //上游
        Observable.just("A", "B", "C")
        //变换操作符
        .concatMap(new Function<String, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(s + " 下标：" + (i + 1) );
                }
                return Observable.fromIterable(list).delay(1, TimeUnit.SECONDS); //创建型操作符，创建被观察者
            }
        })
        //订阅
        .subscribe(new Consumer<Object>() { //下游
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "下游 accept：" + o.toString() );
                // D/TransformationOperatorActivity: 下游 accept：A 下标：1
                // D/TransformationOperatorActivity: 下游 accept：A 下标：2
                // D/TransformationOperatorActivity: 下游 accept：A 下标：3
                // D/TransformationOperatorActivity: 下游 accept：B 下标：1
                // D/TransformationOperatorActivity: 下游 accept：B 下标：2
                // D/TransformationOperatorActivity: 下游 accept：B 下标：3
                // D/TransformationOperatorActivity: 下游 accept：C 下标：1
                // D/TransformationOperatorActivity: 下游 accept：C 下标：2
                // D/TransformationOperatorActivity: 下游 accept：C 下标：3
            }
        });
    }

    /**
     * groupBy 分组变换操作符
     * @param view
     */
    public void r05(View view) {
        //上游
        Observable
                .just(600, 700, 800, 900, 1000, 1400)
                //分组变换操作符
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer > 800 ? "高端配置电脑" : "中端配置电脑"; //分组
                    }
                })
                //使用groupBy下游是有标准的
                .subscribe(new Consumer<GroupedObservable<String, Integer>>() {
                    @Override
                    public void accept(final GroupedObservable<String, Integer> groupedObservable) throws Exception {
                        Log.d(TAG, "accept: " + groupedObservable.getKey());
                        // D/TransformationOperatorActivity: accept: 中端配置电脑
                        // D/TransformationOperatorActivity: accept: 高端配置电脑
                        //以上代码还不能把信息打印全，只是拿到了分组的key

                        //细节 GroupedObservable 被观察者
                        groupedObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d(TAG, "accept  类别：" + groupedObservable.getKey() + "  价格：" + integer);
                                // accept: 中端配置电脑
                                // accept  类别：中端配置电脑  价格：600
                                // accept  类别：中端配置电脑  价格：700
                                // accept  类别：中端配置电脑  价格：800
                                // accept: 高端配置电脑
                                // accept  类别：高端配置电脑  价格：900
                                // accept  类别：高端配置电脑  价格：1000
                                // accept  类别：高端配置电脑  价格：1400
                            }
                        });
                    }
                });

    }

    /**
     * buffer变换操作符：有很多的数据，但不想全部一起发射出去，可以分批次，先缓存到Buffer
     * @param view
     */
    public void r06(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        for (int i = 0; i < 100; i++) {
                            e.onNext(i);
                        }
                        e.onComplete();
                    }
                })
                //变换 buffer
                .buffer(20)
                //订阅
                .subscribe(new Consumer<List<Integer>>() { //下游
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d(TAG, "accept: " + integers);
                // accept: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
                // accept: [20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39]
                // accept: [40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59]
                // accept: [60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79]
                // accept: [80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99]
            }
        });
    }
}
