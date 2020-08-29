package com.sty.ne.rxjavastudy.observer_pattern;

/**
 * @Author: tian
 * @UpdateDate: 2020-08-27 21:19
 */
// 观察者标准
public interface Observer {

    /**
     * 收到 被观察者 发生改变
     * @param observableInfo
     * @param <T>
     */
    <T> void changeAction(T observableInfo);
}
