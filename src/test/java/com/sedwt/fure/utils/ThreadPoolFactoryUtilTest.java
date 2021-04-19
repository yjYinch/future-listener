package com.sedwt.fure.utils;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : zhang yijun
 * @date : 2021/4/19 10:42
 * @description : TODO
 */

public class ThreadPoolFactoryUtilTest {

    @Test
    public void test(){
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hashcode="+ ThreadPoolFactoryUtil.getExecutor().hashCode());
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hashcode="+ ThreadPoolFactoryUtil.getExecutor().hashCode());
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hashcode="+ ThreadPoolFactoryUtil.getExecutor().hashCode());
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hashcode="+ ThreadPoolFactoryUtil.getExecutor().hashCode());
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hashcode="+ ThreadPoolFactoryUtil.getExecutor().hashCode());
            }
        });

    }
}
