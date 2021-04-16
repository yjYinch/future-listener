package com.sedwt.fure.listener;

import com.sedwt.fure.future.ExtensionFuture;

import java.util.concurrent.ExecutionException;

/**
 * @author : zhang yijun
 * @date : 2021/4/16 11:26
 * @description : TODO
 */
@FunctionalInterface
public interface FutureListener<V> {
    /**
     * 操作完成时回调
     * @param future
     * @throws ExecutionException
     * @throws InterruptedException
     */
    void operationComplete(ExtensionFuture<V> future) throws ExecutionException, InterruptedException;
}
