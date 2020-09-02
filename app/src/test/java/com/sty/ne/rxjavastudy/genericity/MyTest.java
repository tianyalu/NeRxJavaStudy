package com.sty.ne.rxjavastudy.genericity;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/1 9:26 PM
 */
//此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
//在实例化泛型类时，必须指定T的具体类型
public class MyTest<T> {
    //t这个成员变量的类型为T,T的类型由外部指定
    private T t;

    public MyTest() {

    }

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
