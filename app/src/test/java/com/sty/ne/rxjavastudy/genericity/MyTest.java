package com.sty.ne.rxjavastudy.genericity;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/1 9:26 PM
 */
public class MyTest<T> {
    private T t;

    public void add(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }
}
