package com.sty.ne.rxjavastudy.observer_pattern;

/**
 * @Author: tian
 * @UpdateDate: 2020-08-27 21:18
 */
//被观察者标准
public interface Observable {

    /**
     * 在被观察者中 注册 观察者
     * @param observer
     */
    void registerObserver(Observer observer);

    /**
     * 在被观察者中 移除 观察者
     * @param observer
     */
    void removeObserver(Observer observer);

    /**
     * 在被观察者中 通知 所有注册的观察者
     */
    void notifyObservers();
}
