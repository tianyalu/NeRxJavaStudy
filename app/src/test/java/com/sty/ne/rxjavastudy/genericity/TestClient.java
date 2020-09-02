package com.sty.ne.rxjavastudy.genericity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/1 9:14 PM
 */
public class TestClient<T> {
    public static void main(String[] args) {

    }

    public void test() {
        T t = null;
        t.hashCode(); //能够调用Object里面的方法
    }

    /**
     * 不使用泛型，容易出现类型转换错误
     */
    @Test
    public void testList() {
        List list = new ArrayList();
        list.add("A");
        list.add(1);
        list.add(6.7);

        Object o = list.get(1);
        String s = (String) o; //运行期异常：java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
    }

    /**
     * 泛型出现后，编译期检查，避免出错
     */
    @Test
    public void testGenericityList() {
        List<String> list = new ArrayList<>();
        list.add("a");
        //list.add(1); //编译期异常

        String s = list.get(0);
    }

    @Test
    public void testGenericExtend() {
        //MyTest<Worker> test = null;
        //test.add(new Worker());

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

        //读写模式
        //可读模式
        MyTest<? extends Person> test1 = null;
        //test1.add(new Person()); //不可写
        //test1.add(new Student()); //不可写
        //test1.add(new Object()); //不可写
        //test1.add(new StudentStub()); //不可写
        Person p = test1.getT();//可读

        //可写模式 不完全可读
        MyTest<? super Person> test2 = null;
        test2.add(new Person()); //可写
        test2.add(new Student()); //可写
        test2.add(new Worker()); //可写
        //test2.add(new Object()); //父类不可写
        Object o = test2.getT(); //不完全可读(需要强转)

    }

    /**
     * extends 上限(限制最高的类为Person) Person or Person的子类都可以使用(最高的类型只能是Person）
     * @param test
     * @param <T>：<T>非常重要，可以理解为声明此方法为泛型方法;<T>表明该方法将使用泛型类型T，此后才可以在方法中使用泛型类型T
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
}
