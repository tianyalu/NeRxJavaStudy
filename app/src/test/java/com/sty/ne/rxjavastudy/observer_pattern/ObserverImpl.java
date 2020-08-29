package com.sty.ne.rxjavastudy.observer_pattern;

/**
 * @Author: tian
 * @UpdateDate: 2020-08-27 21:26
 */
public class ObserverImpl implements Observer {
    @Override
    public <T> void changeAction(T observableInfo) {
        System.out.println(observableInfo);
    }
}
