package com.example.score.util;

/**
 * @Classname Tuple2
 * @Description 元组
 * @Date 2021/4/9
 * @Author hdw
 */
public class Tuple2<T1, T2> {
    private T1 _1;
    private T2 _2;

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T1 get_1() {
        return _1;
    }

    public void set_1(T1 _1) {
        this._1 = _1;
    }

    public T2 get_2() {
        return _2;
    }

    public void set_2(T2 _2) {
        this._2 = _2;
    }
}
