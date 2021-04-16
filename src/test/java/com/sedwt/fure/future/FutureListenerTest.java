package com.sedwt.fure.future;

import com.sedwt.fure.listener.FutureListener;

import java.util.concurrent.ExecutionException;

/**
 * @author : zhang yijun
 * @date : 2021/4/16 13:44
 * @description : TODO
 */

public class FutureListenerTest {

    /**
     * 1. 创建CustomFuture对象
     * 2. 添加
     * @param args
     */

    public static void main(String[] args) {
        new FutureListenerTest().add(3000, 10, 10).addListener(new FutureListener<Integer>() {
            @Override
            public void operationComplete(ExtensionFuture<Integer> future) throws ExecutionException, InterruptedException {
                if (future.isSuccess()) {
                    Integer sum = future.get();
                    System.out.println("结果值为：" + (int) sum);
                } else {
                    System.out.println("任务执行失败，原因="+future.cause());
                }
            }
        });
        System.out.println("主线程结束啦！！！");
    }

    public CustomFuture<Integer> add(long delay, int a, int b) {
        CustomFuture<Integer> future = new CustomFuture<>();
        new Thread(new DelayAddition(delay, a, b, future)).start();
        return future;
    }

    private static class DelayAddition implements Runnable {

        private final long delay;
        private final int a;
        private final int b;
        private final CustomFuture<Integer> future;

        public DelayAddition(long delay, int a, int b, CustomFuture<Integer> future) {
            super();
            this.delay = delay;
            this.a = a;
            this.b = b;
            this.future = future;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delay);
                int sum = a + b;
                future.setSuccess(sum);
            } catch (InterruptedException e) {
                e.printStackTrace();
                future.setFailure(e);
            }
        }
    }
}
