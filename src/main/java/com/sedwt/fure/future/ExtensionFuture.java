package com.sedwt.fure.future;

import com.sedwt.fure.listener.FutureListener;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Future的扩展类：提供了一系列任务的方法
 *
 * V：任务结果的参数类型
 *
 * @author : zhang yijun
 * @date : 2021/4/16 10:58
 */
public interface ExtensionFuture<V> extends Future<V> {
    /**
     * 判断任务是否执行成功
     * @return
     */
    boolean isSuccess();

    /**
     * 立即返回任务结果，不管任务是否已经完成
     * @return
     */
    V getNow();

    /**
     * 任务执行失败时的异常原因
     * @return
     */
    Throwable cause();

    /**
     * 是否可以取消
     * @return
     */
    @Override
    boolean isCancelled();

    /**
     * 等待future的完成
     * @return
     * @throws InterruptedException
     */
    ExtensionFuture<V> await() throws InterruptedException;

    /**
     * 超时等待任务完成
     * @param timeoutMillis
     * @return
     */
    boolean await(long timeoutMillis) throws InterruptedException;

    /**
     * 超时等待任务完成
     * @param timeoutMillis
     * @param timeUnit
     * @return
     */
    boolean await(long timeoutMillis, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 超时等待，不响应中断
     * @param timeOutMills
     * @return
     */
    boolean awaitUninterruptibly(long timeOutMills);

    /**
     * 超时等待，不响应中断
     * @param timeOut
     * @param timeUnit
     * @return
     */
    boolean awaitUninterruptibly(long timeOut, TimeUnit timeUnit);

    /**
     * 添加listener监听器
     * @param listener
     * @return
     */
    ExtensionFuture<V> addListener(FutureListener<V> listener);

    /**
     * 移除listener监听器
     * @param listener
     * @return
     */
    ExtensionFuture<V> removeListener(FutureListener<V> listener);
}
