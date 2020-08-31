package com.sty.ne.rxjavastudy;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 线程切换
 * @Author: tian
 * @UpdateDate: 2020-08-28 16:45
 */
public class ThreadSwitchActivity extends AppCompatActivity {
    private static final String TAG = ThreadSwitchActivity.class.getSimpleName();
    private final String IMAGE_URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598875103852&di=857f506bf750627f082e0676048c8146&imgtype=0&src=http%3A%2F%2Fp0.ssl.cdn.btime.com%2Ft01a6c57604f319e2ee.jpg%3Fsize%3D400x400";
    private ImageView ivImage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_switch);
        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());
        initView();
    }

    private void initView() {
        ivImage = findViewById(R.id.iv_image);

    }

    /**
     * Schedulers.io() : 代表io流操作、网络操作、文件流等耗时操作
     * Schedulers.newThread() : 比较普通的
     * Schedulers.computation() : 代表CPU 大量计算 所需要的线程
     *
     * AndroidSchedulers.mainThread() : 专门为Android main 线程量身定做的
     * @param view
     */
    public void r01(View view) {
        //上游
        //RxJava如果不指定，默认运行在主线程 main
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        Log.d(TAG, "上游 subscribe: " + Thread.currentThread().getName());
                        //上游 subscribe: RxCachedThreadScheduler-1
                        e.onNext("");
                    }
                })
                .subscribeOn(Schedulers.io()) //给上游配置多次，只会在第一次切换，后面的会被忽略
                    .subscribeOn(Schedulers.io()) //忽略
                    .subscribeOn(Schedulers.io()) //忽略
                .observeOn(AndroidSchedulers.mainThread()) //给下游配置多次，每次都会切换
                    .observeOn(AndroidSchedulers.mainThread()) //切换一次
                    .observeOn(AndroidSchedulers.mainThread()) //切换一次
                    //.observeOn(Schedulers.io()) //切换一次
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "下游 accept: " + Thread.currentThread().getName());
                //下游 accept: main
            }
        });
    }

    /**
     * @param view
     */
    public void r02(View view) {
        //上游
        Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        Log.d(TAG, "subscribe: 上游发送了一次 1");
                        e.onNext(1);
                        Log.d(TAG, "subscribe: 上游发送了一次 2");
                        e.onNext(2);
                        Log.d(TAG, "subscribe: 上游发送了一次 3");
                        e.onNext(3);
                    }
                })
                .subscribeOn(Schedulers.io()) //给上游分配子线程
                .observeOn(AndroidSchedulers.mainThread()) //给下游分配主线程
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "下游 accept: " + integer);
                        // 默认情况下当上游和下游都是主线程时（同步）
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 1
                        // D/ThreadSwitchActivity: 下游 accept: 1
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 2
                        // D/ThreadSwitchActivity: 下游 accept: 2
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 3
                        // D/ThreadSwitchActivity: 下游 accept: 3

                        //当上游是子线程下游是主线程时
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 1
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 2
                        // D/ThreadSwitchActivity: subscribe: 上游发送了一次 3
                        // D/ThreadSwitchActivity: 下游 accept: 1
                        // D/ThreadSwitchActivity: 下游 accept: 2
                        // D/ThreadSwitchActivity: 下游 accept: 3
                    }
                });
    }

    /**
     * 不使用RxJava加载图片
     * @param view
     */
    public void r03(View view) {
        //上游
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(IMAGE_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    int responseCode = httpURLConnection.getResponseCode();
                    if(HttpURLConnection.HTTP_OK == responseCode) {
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        Message message = handler.obtainMessage();
                        message.obj = bitmap;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            ivImage.setImageBitmap(bitmap);
            //隐藏加载框
            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            return false;
        }
    });

    /**
     * 使用RxJava加载图片
     * @param view
     */
    public void r04(View view) {
        //上游
        Observable
                .just(IMAGE_URL) //内部发射
                //根据URL下载图片，得到bitmap
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        try {
                            URL url = new URL(IMAGE_URL);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setConnectTimeout(5000);
                            int responseCode = httpURLConnection.getResponseCode();
                            if(HttpURLConnection.HTTP_OK == responseCode) {
                                Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                                return bitmap;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                //给图像的bitmap加水印
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        //给图片加水印
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setTextSize(32);
                        Bitmap bitmapWatermark = drawTextToBitmap(bitmap, "萌萌哒", paint, 60, 60);
                        return bitmapWatermark;
                    }
                })
                //记录日志
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        Log.d(TAG, "apply: 下载的bitmap是这个样子的 " + bitmap);
                        return bitmap;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressDialog = new ProgressDialog(ThreadSwitchActivity.this);
                        progressDialog.setMessage("加载中...");
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.d(TAG, "onNext: ");
                        if(ivImage != null) {
                            ivImage.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onError(Throwable e) { //发生了异常
                        //加载默认图片
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        if(progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    //图片上绘制文字
    private Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        paint.setDither(true); //获取更清晰的图像采样
        paint.setFilterBitmap(true); //过滤一些
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }
}
