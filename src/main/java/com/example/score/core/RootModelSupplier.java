package com.example.score.core;

/**
 * @Description TODO
 * @Date 2021/4/4 14:50
 * @Created by hdw
 */
public interface RootModelSupplier<T extends RootModel> {
    Class<T> rootModelClass();
    T create(Object id);
}
