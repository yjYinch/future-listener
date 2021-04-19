package com.sedwt.fure.normal;

import com.sedwt.fure.utils.ThreadPoolFactoryUtil;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 常规调用异步线程
 *
 * @author : zhang yijun
 * @date : 2021/4/16 16:43
 */

public class NormalGetTest {

    @Test
    public void testAsyncResult() {
        ExecutorService executor = ThreadPoolFactoryUtil.getExecutor();
        Future<Integer> future1 = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(2000);
                return 1;
            }
        });
        Future<Integer> future2 = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(3000);
                return 2;
            }
        });

        while (true) {
            if (future1.isDone() && future2.isDone()) {
                System.out.println("任务都已完成...关闭线程池");
                if (!executor.isShutdown()) {
                    executor.shutdownNow();
                    break;
                }
            }
            if (future1.isDone()) {
                try {
                    System.out.println("任务1[" + Thread.currentThread().getName() + "]已完成，结果=" + future1.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (future2.isDone()) {
                try {
                    System.out.println("任务2[" + Thread.currentThread().getName() + "]已完成，结果=" + future2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
