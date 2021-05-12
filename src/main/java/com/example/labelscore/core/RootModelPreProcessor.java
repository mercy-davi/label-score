package com.example.labelscore.core;

/**
 * @Description TODO
 * @Date 2021/4/4 12:11
 * @Created by hdw
 */
public interface RootModelPreProcessor<T extends RootModel> {
    Class<T> rootModelClass();
    void process(T rootModel);
}
