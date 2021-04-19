package com.sedwt.fure.utils;

import javax.print.DocFlavor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : zhang yijun
 * @date : 2021/4/19 9:23
 * @description : TODO
 */

public class ThreadPoolFactoryUtil {

    private static final int CORE_SIZE = 2;

    private static final int MAX_POOL_SIZE = 5;

    private static final int QUEUE_CAPACITY = 10;

    private static final int KEEP_ALIVE_TIME = 10;

    private volatile static ExecutorService executorService;

    private ThreadPoolFactoryUtil(){
    }

    public static ExecutorService getExecutor(){
        if (executorService == null){
            synchronized (ThreadPoolFactoryUtil.class){
                if (executorService == null){
                    executorService = new ThreadPoolExecutor(
                            CORE_SIZE,
                            MAX_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                            new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        return executorService;
    }

}
