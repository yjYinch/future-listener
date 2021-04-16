# future-listener
使用listener监听器的方式来获取异步执行的结果，当任务完成时触发监听器

**quick start**

1. 创建一个CustomFuture对象

2. 添加监听器Listener，重写operationComplete方法

3. 执行任务

```java
public class ThreadPoolFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CustomFuture<String> future = new CustomFuture<>();
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

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    int i = 1/0;
                    String str = "hello";
                    future.setSuccess(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    future.setFailure(e);
                }
            }
        });

        System.out.println("主线程执行完毕");
    }
}
```

