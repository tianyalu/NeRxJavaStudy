# `RxJava`学习

[TOC]

## 一、基础概念

### 1.1 `RxJava`

> 1. 基于事件流编程，一旦满足“起点”-->“终点”这样的需求，都可以使用`Rxjava`来实现；
> 2. 区别于标准的观察者模式（一个被观察者对应**多个观察者**），`RxJava`是改装的观察者设计模式（一个订阅/注册对应**一个观察者**）。

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_incident_flow.png)

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_begin_and_end.png)

### 1.2 观察者设计模式

标准的观察者设计模式是一个被观察者对应多个观察者，但被观察者发生变化时通知所有观察者，如下图所示

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/standard_observer_scheme.png)

### 1.3 上游与下游

`RxJava`的事件流向是必然从上游流向下游的，不可能反向流动。

> 1. 上游`Observable`被观察者，下游`Observer`观察者（`Consumer`是`Observer`的简化版）；
> 2. `ObservableEmitter<Integer> emitter`发射器 发射事件；
> 3. 可以拆分来写，也可以链式调用。

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_upstream_and_downstream.png)

#### 1.3.1 `RxJava`流程

```java
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
```

#### 1.3.2 结论

* 在 `onComplete()/onError()` 发射完成之后再发射事件，此时下游不再接收上游的事件；
* 已经发射了`onComplete()`，再发射`onError()`，`RxJava`会报错，不允许；
* 先发射`onError()`，再发射`onComplete()`，不会报错，但此时`onComplete()`不会被下游接收到了；
* `RxJava`可以切断下游，让下游不再接收上游发射的事件。

## 二、基本操作

`RxJava`操作符：实质上仅仅是 静态方法/`API` 的调用。

`RxJava`改变开发者的思想，学习`RxJava`等价于学习`Java`编程，学习`RxJava`语法等价于学习操作符`API`。如果学会了所有的操作等价于学会了`RxJava`所有的语法，`RxJava`的学习才真正入门。

### 2.1 创建型操作符

创建型操作符的目的只有一个：创建`Observable`。

常用的创建型操作符包括：`create`，`just`，`formArray`，`empty`，`range`等。

> 1. `create`：使用者自己发射事件；
> 2. `just`：内部自己发射事件，单一对象，变长参数；
> 3. `fromArray`：内部自己发射事件，数集对象；
> 4. `empty`：内部自己发射事件，只会发射`omComplete()`事件，无法发射有值事件，下游默认是`Object`；
> 5. `range`：内部自己发射事件，`start 1, count 5` 最终结果（1，2，3，4，5）。

#### 2.1.1 `create`

使用者自己发射事件。

```java
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
```

#### 2.1.2 `just`

内部自己发射事件，单一对象，变长参数。

```java
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
```

#### 2.1.3 `fromArray`

内部自己发射事件，数集对象。

```java
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
```

#### 2.1.4 `empty`

内部自己发射事件，只会发射`omComplete()`事件，无法发射有值事件，下游默认是`Object`。

```java
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
```

#### 2.1.5 `range`

内部自己发射事件，`start 1, count 5` 最终结果（1，2，3，4，5）。

```java
public void r05(View view) {
  // 上游
  // Observable.range(1, 8) //range内部会去发射  1 2 3 4 5 6 7 8 从1开始加 数量共8个
  Observable.range(80, 5) //range内部会去发射  80 81 82 83 84 从80开始加 数量共5个
    .subscribe( //订阅
    new Consumer<Integer>() { //下游
      @Override
      public void accept(Integer integer) throws Exception {
        Log.d(TAG, "onNext: " + integer);
      }
    });
}
```

### 2.2 变换型操作符

变换型操作符在上游和下游之间实现变换操作。

常用的变换型操作符包括：`map`，`flatMap`，`concatMap`，`groupBy`，`buffer`等。

> 1. `map`：上一层`int`，把`int`变换成`String`-->观察者`String`类型；
> 2. `flatMap`：上一层`int`，把`int`变换成`ObservableSource<String>`(还可以再次发射多次事件) --> 观察者`String`类型 **【不排序】**；
> 3. `concatMap`：上一层`int`，把`int`变成`ObservableSource<Bitmap>`(还可以再次发射多次事件) --> 观察者`String`类型 **【排序】**； 
> 4. `groupBy`：上一层`int`，把`int`变成`String`(高端配置电脑) --> 观察者`GroupedObservable`类型(`key`=“高端配置电脑”，细节再包裹一层)；
> 5. `buffer`：100个事件 `Integer`,`.buffer(20)` --> 观察者`List<Integer>` == 5个集合。

#### 2.2.1 `map`

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_transformation_operator_map.png)

```java
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
```

#### 2.2.2 `flatMap`

`flatMap`变换后得到的是`ObservableSource`对象，该对象可以再次（可多次）发射事件。`flatMap`是无序的。

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_transformation_operator_flatmap.png)

测试代码如下：

```java
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
            e.onNext(integer + " flatMap变换操作符1");
            e.onNext(integer + " flatMap变换操作符2");
            e.onNext(integer + " flatMap变换操作符3");
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
```

这里发现一个问题，如果在`flatMap`中发射完全相同的内容（即不加1，2，3后缀的话）会出现如下打印内容（也有可能第二行的意思是内容完全相同，就省略显示1行的打印日志）：

```bash
# 如果发射完全相同的内容会有有如下打印内容
D/TransformationOperatorActivity: 下游接收 变换操作符发射的事件 accept: 111 flatMap变换操作符
I/chatty: uid=10076(com.sty.ne.rxjavastudy) identical 1 lines
D/TransformationOperatorActivity: 下游接收 变换操作符发射的事件 accept: 111 flatMap变换操作符
```

体现无序的例子：

```java
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
```

#### 2.2.3 `concatMap`

`concatMap`是有序的。

```java
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
```

#### 2.2.4 `groupBy`

分组变换操作符：上一层`int`，把`int`变成`String`(高端配置电脑) --> 观察者`GroupedObservable`类型(`key`=“高端配置电脑”，细节再包裹一层)。

```java
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
```

#### 2.2.5 `buffer`

100个事件 `Integer`,`.buffer(20)` --> 观察者`List<Integer>` == 5个集合。

```java
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
```

### 2.3 过滤型操作符

过滤型操作符在上游和下游之间实现过滤操作。

常用的过滤型操作符包括：`filter`，`take`，`distinct`，`elementAt`等。

> 1. `filter`：如果是`false`，全部过滤掉，都不发射给下游；如果是`true`，全部不过滤，都发射给下游；
> 2. `take`：停止定时器，只有在定时器运行基础上加入`take`过滤操作符，才能体现其价值；
> 3. `distinct`：过滤重复事件；
> 4. `elementAt`：指定发射事件内容，如果无法指定，发射默认的事件。

#### 2.3.1 `filter`

如果是`false`，全部过滤掉，都不发射给下游；如果是`true`，全部不过滤，都发射给下游。

```java
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
```

#### 2.3.2 `take`

停止定时器，只有在定时器运行基础上加入`take`过滤操作符，才能体现其价值。

```java
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
```

#### 2.3.3 `distinct`

过滤重复事件。

```java
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
```

#### 2.3.4 `elementAt`

指定发射事件内容，如果无法指定，发射默认的事件。

```java
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
```

### 2.4 条件型操作符

条件型操作符在上游和下游之间实现条件判断操作，和过滤型操作符极其相似。**如果使用了条件操作符，下一层接收的类型就是条件类型`Boolean`**。

常用的条件型操作符包括：`all`，`contains`，`any`，`isEmpty`等。

> 1. `all`：全部为`true`才为`true`，只要有一个为`false`，则返回`false`（&&）；
> 2. `contains`：是否包含；
> 3. `any`：全部为`false`才为`false`，只要有一个为`true`，则返回`true`（||）。

#### 2.4.1 `all`

全部为`true`才为`true`，只要有一个为`false`，则返回`false`（&&）。

```java
public void r01(View view) {
  String v1 = "1";
  String v2 = "2";
  String v3 = "3";
  String v4 = "cc";

  //平常写法
  if("cc".equals(v1) || "cc".equals(v2) || "cc".equals(v3) || "cc".equals(v4)) {
    Log.d(TAG, "r01: " + false);   //false
  }else {
    Log.d(TAG, "r01: " + true);
  }

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
        Log.d(TAG, "accept: " + b);  // false
      }
    });
}
```

#### 2.4.2 `contains`

是否包含。

```java
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
```

#### 2.4.3 `any`

全部为`false`才为`false`，只要有一个为`true`，则返回`true`（||）。

```java
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
```

### 2.5 合并型操作符

合并型操作符可以使两个或多个**被观察者**合并。

常用的合并型操作符包括：`startWith`，`concatWith`，`concat`，`merge`，`zip`。

> 1. `startWith`：先执行被组合（`startWith`括号里面）的被观察者；
> 2. `concatWith`：后执行被组合（`startWith`括号里面）的被观察者；
> 3. `concat`：按照顺序依次执行，最多合并**4个**被观察者；
> 4. `merge`：并列执行（用`intervalRange`演示并列执行），最多合并**4个**被观察者；
> 5. `zip`：需要对应关系，如果不对应，会被忽略掉，最多合并**9个**被观察者。

区别：

> 1. `startWith/concatWith`：先创建被观察者，然后由该被观察者组合其它的被观察者，然后订阅；
> 2. `concat/merge/zip`：直接合并多个已经被创建了的被观察者，然后订阅。

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_differences_among_merge_operator.png)

#### 2.5.1 `startWith`

先执行被组合（`startWith`括号里面）的被观察者。

```java
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
```

#### 2.5.2 `concatWith`

后执行被组合（`startWith`括号里面）的被观察者.

```java
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
```

#### 2.5.3 `concat`

按照顺序依次执行，最多合并**4个**被观察者。

```java
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
```

#### 2.5.4 `merge`

并列执行（用`intervalRange`演示并列执行），最多合并**4个**被观察者。

```java
/**
 * merge 合并操作符 --> 最多能够合并四个被观察者，并列（并发）执行
 * @param view
 */
public void r04(View view) {
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
```

#### 2.5.5 `zip`

需要对应关系，如果不对应，会被忽略掉，最多合并**9个**被观察者。

```java
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
```

### 2.6 异常处理型操作符

异常处理型操作符可以使可以用来处理`Error`和`Exception`。

> `RxJava`中不标准的异常抛出方式：`throw new IllegalAccessError("我要报错了")`；
>
> `RxJava`中标准的异常抛出方式：`e.onError(new IllegalAccessError("我要报错了"))`;

常用的异常处理型操作符包括：`onErrorReturn`，`onErrorResumeNext`，`onExceptionResumeNext`，`retry`。

> 1. `onErrorReturn`：最先拦截到`e.onError`，并且可以给下游返回一个标识400，走`onNext`和`onComplete`；不标准的异常抛出方式会引起程序崩溃；
> 2. `onErrorResumeNext`：最先拦截到`e.onError`，并且可以给下游返回一个被观察者（可以再次发射事件）；不标准的异常抛出方式会引起程序崩溃；
> 3. `onExceptionResumeNext`：能在**异常**的时候扭转乾坤；不标准的异常抛出方式**不会**引起程序崩溃；
> 4. `retry`：`return false`表示不会重试；`return true`表示会重试；可以限制重试次数；也可以打印重试次数。

#### 2.6.1 `onErrorReturn`

最先拦截到`e.onError`，并且可以给下游返回一个标识400，走`onNext`和`onComplete`；不标准的异常抛出方式会引起程序崩溃。

```java
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
            //throw new IllegalAccessError("我要报错了");

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
```

#### 2.6.2 `onErrorResumeNext`

最先拦截到`e.onError`，并且可以给下游返回一个被观察者（可以再次发射事件）；不标准的异常抛出方式会引起程序崩溃。

```java
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
          //throw new IllegalAccessError("我要报错了");
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
```

#### 2.6.3 `onExceptionResumeNext`

能在**异常**的时候扭转乾坤；不标准的异常抛出方式**不会**引起程序崩溃。

```java
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
          //throw new IllegalAccessException("错了"); //用不用onExceptionResumeNext操作符都不会崩溃
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
```

#### 2.6.4 `retry`

`return false`表示不会重试；`return true`表示会重试；可以限制重试次数；也可以打印重试次数。

```java
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
    //.retry(new Predicate<Throwable>() {
    //	@Override
    //	public boolean test(Throwable throwable) throws Exception {
    //		Log.d(TAG, "test: " + throwable.getMessage());
    //		//return false; //代表不去重试
    //		return true; //代表一直重试
    //	}
    //})
    //演示二(重试3次[共执行4次])
    .retry(3, new Predicate<Throwable>() {
      @Override
      public boolean test(Throwable throwable) throws Exception {
        Log.d(TAG, "test: " + throwable.getMessage());
        return true;
      }
    })
    //演示三(打印重试了多少次,计数[无限重试])
    //.retry(new BiPredicate<Integer, Throwable>() {
    //	@Override
    //	public boolean test(Integer integer, Throwable throwable) throws Exception {
    //		Thread.sleep(2);
    //		Log.d(TAG, "retry: 已经重试了：" + integer + "次 e:" + throwable.getMessage());
    //		return true; //重试
    //	}
    //})
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
```

### 2.7 线程切换

#### 2.7.1 异步线程的区别

**`Scheduler`的类型：**

> 1. `Schedulers.io()`：由无限制的线程池支持，主要用于`io`流操作、网络操作、文件流、数据库交互等耗时操作；
> 2. `Schedulers.newThread()`：为每个安排的工作任务创建一个新的线程，不会重复使用，调度昂贵；
> 3. `Schedulers.computation()`：由有限线程池支持，其大小可达处理器的数量；用于计算或`CPU`密集型工作，例如调整图像大小，处理大型数据集等。
> 4. `Schedulers.from(Executor executor)`：创建并返回由指定执行程序支持的自定义调度程序。如果要限制线程池中同时线程的数量，可使用`Scheduler.from(Executors.newThreadPool(n))`，这保证了如果在所有线程都被占用时，调度任务将排队；池中的线程将一直存在，知道它被明确关闭；
> 5. `AndroidSchedulers.mainThread()`：专门为`Android main`线程量身定做的。

**线程切换方式：**

> * `observeOn() `：指定下游运算所在线程（可以多次使用无限切换）;
> * `subscribeOn()`：指定源`Observable`工作（发射事件）执行的线程，一直推送延续到`Observer`（中途可以用`observerOn`切换线程）；它可以在流中的任何位置，如果有多个`subscribeOn`存在，只有第一个生效。

* 给上游分配多次，只会在第一次切换，后面的配置会被忽略掉，不会再切换线程；
* 如果不配置异步线程，上游发射一次，下游接收一次，表现为同步的；
* 如果配置异步线程，表现为异步的（从打印结果看，发射完所有事件后才接收）。

参考：[RxJava线程切换之subscribeOn和observeOn详解](https://www.jianshu.com/p/a3f2c3ee00a3)

#### 2.7.2 下载图片示例

传统的写法方式容易四分五裂，代码看上去比较凌乱；

`RxJava`的写法是基于事件流的，有起点和终点，一个链条完成。

```java
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
```

#### 2.7.3 注册登录流程示例

本例子实现注册请求网络、修改`UI`，然后登录请求网络，修改`UI`，即多次主线程和子线程切换的事件流，

例子1：

```java
private void normalFlowThreadTest() {
  progressDialog = new ProgressDialog(this);
  progressDialog.setMessage("注册中...");
  Observable.just("注册中...")
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        Log.d(TAG, "map1 " + s + " thread: " + Thread.currentThread().getName());
        SystemClock.sleep(2000);
        return "注册成功";
      }
    })
    .observeOn(AndroidSchedulers.mainThread()) //指定下面的call在主线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        tvRegisterUi.setText(s);
        progressDialog.setMessage("登录中...");
        Log.d(TAG, "map2 " + s + " thread: " + Thread.currentThread().getName());
        return "登录中...";
      }
    })
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        Log.d(TAG, "map3 " + s  + " thread: " + Thread.currentThread().getName());
        SystemClock.sleep(2000);
        return "登录成功";
      }
    })
    .subscribeOn(Schedulers.io())  //指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer（中途可以用observerOn切换线程），它可以在流中的任何位置，如果有多个subscribeOn,只有第一个生效
    .observeOn(AndroidSchedulers.mainThread()) //指定下游运算所在线程（可以多次使用无限切换）
    .subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe thread: " + Thread.currentThread().getName());
        progressDialog.show();
      }

      @Override
      public void onNext(String s) {
        tvLoginUi.setText(s);
        Log.d(TAG, "onNext " + s + " thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onError(Throwable e) {
        Log.d(TAG, "onError thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "onComplete thread: " + Thread.currentThread().getName());
        if(progressDialog != null) {
          progressDialog.dismiss();
        }
        // D/RetrofitActivity: onSubscribe thread: main
        // D/RetrofitActivity: map1 注册中... thread: RxCachedThreadScheduler-2
        // D/RetrofitActivity: map2 注册成功 thread: main
        // D/RetrofitActivity: map3 登录中... thread: RxCachedThreadScheduler-3
        // D/RetrofitActivity: onNext 登录成功 thread: main
        // D/RetrofitActivity: onComplete thread: main
      }
    });
}
```

例子2：

```java
private void doOnNextThreadTest() {
  progressDialog = new ProgressDialog(this);
  progressDialog.setMessage("注册中...");
  Observable.just("注册中...")
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        Log.d(TAG, "map1 " + s + " thread: " + Thread.currentThread().getName());
        SystemClock.sleep(2000);
        return "注册成功";
      }
    })
    .observeOn(AndroidSchedulers.mainThread())
    .doOnNext(new Consumer<String>() { //每次在Observer的onNext方法调用之前被调用，但是调用顺序和其在流中的位置顺序一致
      @Override
      public void accept(String s) throws Exception {
        tvRegisterUi.setText(s);
        progressDialog.setMessage("登录中...");
        Log.d(TAG, "doOnNext " + s + " thread: " + Thread.currentThread().getName());
      }
    })
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        String msg = "登录中...";
        Log.d(TAG, "map3 " + msg  + " thread: " + Thread.currentThread().getName());
        SystemClock.sleep(2000);
        return "登录成功";
      }
    })
    .subscribeOn(Schedulers.io())  //指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer（中途可以用observerOn切换线程），它可以在流中的任何位置，如果有多个subscribeOn,只有第一个生效
    .observeOn(AndroidSchedulers.mainThread()) //指定下游运算所在线程（可以多次使用无限切换）
    .subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe thread: " + Thread.currentThread().getName());
        progressDialog.show();
      }

      @Override
      public void onNext(String s) {
        tvLoginUi.setText(s);
        Log.d(TAG, "onNext " + s + " thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onError(Throwable e) {
        Log.d(TAG, "onError thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "onComplete thread: " + Thread.currentThread().getName());
        if(progressDialog != null) {
          progressDialog.dismiss();
        }
        // D/RetrofitActivity: onSubscribe thread: main
        // D/RetrofitActivity: map1 注册中... thread: RxCachedThreadScheduler-2
        // D/RetrofitActivity: doOnNext 注册成功 thread: main
        // D/RetrofitActivity: map3 登录中... thread: RxCachedThreadScheduler-3
        // D/RetrofitActivity: onNext 登录成功 thread: main
        // D/RetrofitActivity: onComplete thread: main
      }
    });
}
```

#### 2.7.4 单纯的线程切换全面测试

```java
private void subscribeOnThreadTest() {
  Observable
    .create(new ObservableOnSubscribe<String>() {
      @Override
      public void subscribe(ObservableEmitter<String> e) throws Exception {
        Log.d(TAG, "subscribe thread: " + Thread.currentThread().getName());
        e.onNext("注册中...");
        e.onComplete();
      }
    })
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        Log.d(TAG, "map1 " + s + " thread: " + Thread.currentThread().getName());
        return "注册成功";
      }
    })
    .observeOn(AndroidSchedulers.mainThread())
    .doOnNext(new Consumer<String>() { //每次在Observer的onNext方法调用之前被调用，但是调用顺序和其在流中的位置顺序一致
      @Override
      public void accept(String s) throws Exception {
        Log.d(TAG, "doOnNext " + s + " thread: " + Thread.currentThread().getName());
      }
    })
    .observeOn(Schedulers.io()) //指定下面的call在子线程执行
    .map(new Function<String, String>() {
      @Override
      public String apply(String s) throws Exception {
        String msg = "登录中...";
        Log.d(TAG, "map3 " + msg  + " thread: " + Thread.currentThread().getName());
        return "登录成功";
      }
    })
    .subscribeOn(Schedulers.io())  //指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer（中途可以用observerOn切换线程），它可以在流中的任何位置，如果有多个subscribeOn,只有第一个生效
    .observeOn(AndroidSchedulers.mainThread()) //指定下游运算所在线程（可以多次使用无限切换）
    .subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onNext(String s) {
        Log.d(TAG, "onNext " + s + " thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onError(Throwable e) {
        Log.d(TAG, "onError thread: " + Thread.currentThread().getName());
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "onComplete thread: " + Thread.currentThread().getName());
        // TODO 仅指定subscribeOn(Schedulers.io())时
        // D/ThreadSwitchActivity: onSubscribe thread: main
        // D/ThreadSwitchActivity: subscribe thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: map1 注册中... thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: doOnNext 注册成功 thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: map3 登录中... thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: onNext 登录成功 thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: onComplete thread: RxCachedThreadScheduler-1

        // TODO 加上指定最下面的observeOn(AndroidSchedulers.mainThread())时
        // D/ThreadSwitchActivity: onSubscribe thread: main
        // D/ThreadSwitchActivity: subscribe thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: map1 注册中... thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: doOnNext 注册成功 thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: map3 登录中... thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: onNext 登录成功 thread: main
        // D/ThreadSwitchActivity: onComplete thread: main

        // TODO 加上所有的线程切换代码时
        // D/ThreadSwitchActivity: onSubscribe thread: main
        // D/ThreadSwitchActivity: subscribe thread: RxCachedThreadScheduler-1
        // D/ThreadSwitchActivity: map1 注册中... thread: RxCachedThreadScheduler-2
        // D/ThreadSwitchActivity: doOnNext 注册成功 thread: main
        // D/ThreadSwitchActivity: map3 登录中... thread: RxCachedThreadScheduler-3
        // D/ThreadSwitchActivity: onNext 登录成功 thread: main
        // D/ThreadSwitchActivity: onComplete thread: main
      }
    });
}
```



### 2.8 背压模式

 **背压模式的由来:**

 `RxJava1.x` 的时候，还没有背压模式，如果上游不停地发射事件，但是下游处理不过来，就会造成内存泄漏；

`RxJava2.x`之后增加了背压模式：`Observable` --> `Flowable`(解决背压)。

**什么时候使用`Observable<Observer>`，什么时候使用`Flowable<Subscriber>`？**

> 当上游发射大量（参考1000）的事件，考虑到下游有可能处理不过来时，使用`Flowable<Observer>`。

#### 2.8.1 背压模式的策略

> 1. `BackpressureStrategy.ERROR`：上游发射大量事件，下游阻塞处理不过来时，放入缓存池，如果池满了，抛出异常；
> 2. `BackpressureStrategy.BUFFER`：上游发射大量事件，下游阻塞处理不过来时，放入缓存池，“等待”下游来接收事件；
> 3. `BackpressureStrategy.DROP`：上游发射大量事件，下游阻塞处理不过来时，放入缓存池，如果池满了，就会把后面发射的事件丢弃掉；
> 4. `BackpressureStrategy.LATEST`：上游发射大量事件，下游阻塞处理不过来时，只存储128个事件。

* 同步时，当上游发射第一个事件，需要等待下游执行完毕后再发射第二个事件，但是由于没有执行`subscription.request(10)`，下游就没有处理第一个事件，上游还在一直等待，直到抛出异常`could not emit value due to lack of requests`，此时调用外部的`subscription.request(10)`是没有反应的；
* 异步时，上游一直在发射事件，不会等待下游，此时调用外部的`subscription.request(10)`是可以取出来给下游处理的；
* 一旦缓存池处理了一次上游的事件，池中的事件数量会-1；

```java
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
```

#### 2.8.2 `Flowable`和`Observable`的区别

`Flowable`是按照`Observable`依葫芦画瓢设计的，所以使用时几乎一模一样，只是类名不同而已，另外`Flowable`增加了背压模式。

> 1. `Observable`和`Observer`对应；`Flowable`和`Subscriber`对应；
> 2. `Observable` <--> `Observer`的`onSubscribe(Disposable d)`可以切断下游；
> 3. `Flowable` <--> `Subscriber` 的onSubscribe(Subscription s)` 需要取出（`s.request(5)`）事件交给下游处理。

##### 2.8.2.1 `Flowable`和`Observable`之`fromArray`

```java
public void r03(View view) {
  String[] strings = {"1", "2", "3"};
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
```

##### 2.8.2.2 `Flowable`和`Observable`之`just`

`Observable` <--> `Observer`的`onSubscribe(Disposable d)`可以切断下游；`Flowable` <--> `Subscriber` 的onSubscribe(Subscription s)` 需要取出（`s.request(5)`）事件交给下游处理。

```java
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
```

##### 2.8.2.3 `Flowable`和`Observable`之`map`

```java
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
        return Flowable.just(bitmap1, bitmap2, bitmap3);  //注意这里
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
```



##### 2.8.2.4 `Flowable`和`Observable`之`create`

`Flowable`增加了背压模式的参数。

```java
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
```

### 2.9 `RxJava`配合`Retrofit`使用

`RxJava`配合`Retrofit`请求网络流程如下图所示：

![image](https://github.com/tianyalu/NeRxJavaStudy/raw/master/show/rxjava_retrofit_request_network_process.png)

本例子实现了按顺序执行两次网络请求以及其之后更新`UI`的操作(可参考2.7.3)，一行代码写完需求流程说明：

> 1. 请求服务器，执行注册操作（耗时 --> 切换异步线程）；
> 2. 更新注册后的所有注册相关UI（main --> 切换主线程）；
> 3. 请求服务器，执行登录操作（耗时 --> 切换异步线程）；
> 4. 更新登录后的所有登录相关UI（main --> 切换主线程）。

从代码执行流程看的事件流：

> 1. `onSubscribe` --> `progressDialog.show()`
> 2. `registerAction(new RegisterRequest())`
> 3. `doOnNext`更新注册后的所有`UI`
> 4. `flatMap`执行登录的耗时操作
> 5. 订阅的观察者执行下游的`onNext`方法，更新所有登录后的`UI`
> 6. `progressDialog.dismiss()`

**`doOnNext()`**是观察者被通知之前(也就是回调之前)会调用的方法，说白了就是最终回调之前的前一个回调方法，这个方法一般做的事件类似于观察者做的事情，只是自己不是最终的回调者（观察者即最终回调者）。

```java
private void requestNetwork2() {
  progressDialog = new ProgressDialog(this);
  progressDialog.setMessage("正在请求中...");

  MyRetrofit.createRetrofit().create(IRequestNetwork.class)
    //1. 请求服务器注册操作 //TODO 第二步
    //IRequestNetwork.loginAction
    .registerAction(new RegisterRequest()) //Observable<RegisterResponse> 上游 被观察者 耗时操作
    .subscribeOn(Schedulers.io())  //指定源Observable工作（发射事件）执行的线程，一直推送延续到Observer（中途可以用observerOn切换线程），它可以在流中的任何位置，如果有多个subscribeOn,只有第一个生效
    .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
    //2. 注册完成后更新注册UI
    .doOnNext(new Consumer<RegisterResponse>() { //每次在Observer的onNext方法调用之前被调用，但是调用顺序和其在流中的位置顺序一致
      @Override
      public void accept(RegisterResponse registerResponse) throws Exception {
        //更新注册相关的所有UI //TODO 第三步
        tvRegisterUi.setText("注册成功");
      }
    })
    //3. 马上去登录服务器操作
    .observeOn(Schedulers.io()) //给下游切换子线程
    .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
      @Override
      public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
        //这里还可以拿到注册后的响应对象RegisterResponse
        //执行登录服务器操作 //TODO 第四步
        Observable<LoginResponse> observable = MyRetrofit.createRetrofit().create(IRequestNetwork.class)
          .loginAction(new LoginRequest());
        return observable;
      }
    })
    //4. 登录完成之后更新登录的UI
    .observeOn(AndroidSchedulers.mainThread()) //给下游切换主线程
    .subscribe(new Observer<LoginResponse>() {
      @Override
      public void onSubscribe(Disposable d) {
        //TODO 第一步
        progressDialog.show();
      }

      @Override
      public void onNext(LoginResponse loginResponse) {
        //更新登录相关的所有UI //TODO 第五步
        tvLoginUi.setText("登录成功");
      }

      @Override
      public void onError(Throwable e) {
      }

      @Override
      public void onComplete() {
        //TODO 第六步
        if(progressDialog != null) {
          progressDialog.dismiss();
        }
      }
    });
}
```

### 2.10 `Java`泛型

泛型：即“参数化类型”，就是将类型由原来的具体的类型参数化；如果我们不指定泛型类型，默认就是`Object`类型，是`Object`的扩展型。

泛型只在编译阶段有效，在编译之后程序会采取去泛型化的措施。

测试类的继承关系：

```java
StudentStub extends Student extends Person;
Worker extends Person;
```

`MyTest`类：

```java
//此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
//在实例化泛型类时，必须指定T的具体类型
public class MyTest<T> {
    //t这个成员变量的类型为T,T的类型由外部指定
    private T t;

    public MyTest(T t) { //泛型构造方法形参t的类型也为T，T的类型由外部指定
        this.t = t;
    }

    public void add(T t) {
        this.t = t;
    }

    public T getT() { //泛型方法getT的返回值类型为T，T的类型由外部指定
        return t;
    }
}
```

参考：[java 泛型详解-绝对是对泛型方法讲解最详细的，没有之一](https://www.cnblogs.com/icebutterfly/p/9012858.html)

#### 2.10.1 上限和下限

> 1. `? extends F`：上限，限制最高类为F，F和F的子类都可以使用，其父类不能使用；
> 2. `? super F`：下限，限制最低类为F，F和F的父类都可以使用，其子类不能使用。

```java
    public void testGenericExtend() {
        //上限
        //show(new MyTest<Object>()); //Person的父类，会报错
        show1(new MyTest<Person>());
        show1(new MyTest<Worker>());
        show1(new MyTest<Student>());

        //下限
        show2(new MyTest<Student>());
        show2(new MyTest<Person>()); //父类
        show2(new MyTest<Object>()); //父类
        //show2(new MyTest<StudentStub>()); //Student的子类，会报错
    }    

    /**
     * extends 上限(限制最高的类为Person) Person or Person的子类都可以使用(最高的类型只能是Person）
     * @param test
     * @param <T>：<T>非常重要，可以理解为声明此方法为泛型方法;
     *   					<T>表明该方法将使用泛型类型T，此后才可以在方法中使用泛型类型T
     *             只有声明了<T>的方法才是泛型方法，泛型类中使用了泛型的成员方法并不是泛型方法
     */
    public static <T> void show1(MyTest<? extends Person> test) {

    }

    /**
     * super 下限(限制最低的类为Student) Student or Student的父类都可以使用(最低的类型只能是Student）
     * @param test
     * @param <T>
     */
    public static <T> void show2(MyTest<? super Student> test) {

    }
```



#### 2.10.3 读写模式

> 1. `<? extends F>`：可读模式，不可写；
> 2. `<? super F>`：可写模式，不完全可读。

```java
    public void testGenericExtend() {
        //读写模式
        //可读模式
        MyTest<? extends Person> test1 = null;
        test1.add(new Person()); //不可写
        test1.add(new Student()); //不可写
        test1.add(new Object()); //不可写
        test1.add(new StudentStub()); //不可写
        Person p = test1.getT();//可读

        //可写模式 不完全可读
        MyTest<? super Person> test2 = null;
        test2.add(new Person()); //可写
        test2.add(new Student()); //可写
        test2.add(new Worker()); //可写
        test2.add(new Object()); //父类不可写
        Object o = test2.getT(); //不完全可读(需要强转)

    }
```

#### 2.10.4 区分读写模式和上限下限

在方法定义/声明的参数中使用时，一定是上限和下限，如`show2(MyTest<? Super Student> myTest)`；

在真正使用到泛型时/真正方法调用时，就是读写模式，如`test2.add(new Student())`。

