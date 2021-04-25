package com.sedwt.fure.future;

import com.sedwt.fure.listener.FutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author : zhang yijun
 * @date : 2021/4/16 14:54
 * @description : TODO
 */

public class ThreadPoolFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CustomFuture<String> future = new CustomFuture<>();


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    String str = "hello";
                    future.setSuccess(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    future.setFailure(e);
                }
            }
        });

        Thread.sleep(10000);

        future.addListener(new FutureListener<String>() {
            @Override
            public void operationComplete(ExtensionFuture<String> future) throws ExecutionException, InterruptedException {
                if (future.isSuccess()) {
                    System.out.println("获得执行结果：" + future.get());
                } else {
                    System.out.println("任务执行失败："+ future.cause());
                }
                // 关闭线程池
                if (!executorService.isShutdown()){
                    executorService.shutdownNow();
                }
            }
        });

        System.out.println("主线程执行完毕");
    }
}
