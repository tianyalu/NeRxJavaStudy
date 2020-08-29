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

