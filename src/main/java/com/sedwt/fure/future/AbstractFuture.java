package com.sedwt.fure.future;

import com.sedwt.fure.cause.CancelException;
import com.sedwt.fure.cause.CauseHolder;
import com.sedwt.fure.listener.FutureListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 *
 *
 * @author : zhang yijun
 * @date : 2021/4/16 11:39
 * @description : TODO
 */

public abstract class AbstractFuture<V> implements ExtensionFuture<V> {

    /**
     * 保存任务的执行结果
     */
    protected volatile Object result;

    /**
     * 监听器集合，存储FutureListener
     */
    protected List<FutureListener<V>> listeners = new CopyOnWriteArrayList<>();

    /**
     * 标记位，当setSuccess(null)是，指向它
     */
    private static final Object RESULT_NULL_FLAG = new Object();

    /**
     * 当任务执行结果!=null, 且任务结果不是异常
     * @return
     */
    @Override
    public boolean isSuccess() {
        return result != null && !(result instanceof CauseHolder);
    }

    /**
     * 调用get方法获取异步执行结果
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        // 判断任务是否执行完成，如果没有执行完，判断该线程是否被中断，如果该线程没有被中断，且还未完成，就进入await状态，
        // 等待其它线程调用本对象的notify/notifyAll方法
        await();

        Throwable cause = cause();
        if(cause == null){
            // 当无异常时，获取result的值
            return getNow();
        }
        // 有异常时，抛出异常
        throw new ExecutionException(cause);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getNow() {
        return (V) (result == RESULT_NULL_FLAG ? null : result);
    }

    @Override
    public ExtensionFuture<V> await() throws InterruptedException {
        return realAwait(true);
    }

    /**
     * 首先判断
     * @param interruptable
     * @return
     * @throws InterruptedException
     */
    private ExtensionFuture<V> realAwait(boolean interruptable) throws InterruptedException {
        // 若已完成就直接返回了
        if (!isDone()) {
            // 若允许终端且被中断了则抛出中断异常
            if (interruptable && Thread.interrupted()) {
                throw new InterruptedException("thread " + Thread.currentThread().getName() + " has been interrupted.");
            }

            boolean interrupted = false;
            synchronized (this) {
                while (!isDone()) {
                    try {
                        wait(); // 释放锁进入waiting状态，等待其它线程调用本对象的notify()/notifyAll()方法
                    } catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        } else {
                            interrupted = true;
                        }
                    }
                }
            }

            if (interrupted) {
                // 为什么这里要设中断标志位？因为从wait方法返回后, 中断标志是被clear了的,
                // 这里重新设置以便让其它代码知道这里被中断了。
                Thread.currentThread().interrupt();
            }
        }
        return this;
    }

    private boolean realAwait(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (isDone()) {
            return true;
        }

        if (timeoutNanos <= 0) {
            return isDone();
        }

        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        long startTime = timeoutNanos <= 0 ? 0 : System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;

        try {
            synchronized (this) {
                if (isDone()) {
                    return true;
                }

                if (waitTime <= 0) {
                    return isDone();
                }

                for (;;) {
                    try {
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        } else {
                            interrupted = true;
                        }
                    }

                    if (isDone()) {
                        return true;
                    } else {
                        waitTime = timeoutNanos - (System.nanoTime() - startTime);
                        if (waitTime <= 0) {
                            return isDone();
                        }
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean await(long timeOutMills) throws InterruptedException {
        return realAwait(TimeUnit.MILLISECONDS.toNanos(timeOutMills), true);
    }

    @Override
    public boolean await(long timeOut, TimeUnit timeUnit) throws InterruptedException {
        return realAwait(timeUnit.toNanos(timeOut), true);
    }

    public ExtensionFuture<V> awaitUninterruptibly() throws Exception {
        try {
            return realAwait(false);
        } catch (InterruptedException e) { // 这里若抛异常了就无法处理了
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeOutMills) {
        try {
            return realAwait(TimeUnit.MILLISECONDS.toNanos(timeOutMills),false);
        } catch (InterruptedException e) { // 这里若抛异常了就无法处理了
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeOut, TimeUnit timeUnit) {
        try {
            return realAwait(timeUnit.toNanos(timeOut), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }


    @Override
    public ExtensionFuture<V> addListener(FutureListener<V> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        // 若已完成直接通知该监听器
        if (isDone()) {
            notifyListener(listener);
            return this;
        }
        synchronized (this) {
            if (!isDone()) {
                listeners.add(listener);
                return this;
            }
        }
        notifyListener(listener);
        return this;
    }

    /**
     * 移除listener监听器
     *
     * @param listener
     * @return
     */
    @Override
    public ExtensionFuture<V> removeListener(FutureListener<V> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }

        if (!isDone()) {
            listeners.remove(listener);
        }
        return this;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(isDone()){
            return false;
        }

        synchronized (this){
            if(isDone()){
                return false;
            }

            result = new CauseHolder(new CancelException());
            notifyAll();
            notifyListeners();
        }

        return false;
    }

    @Override
    public boolean isCancelled() {
        return result != null && result instanceof CauseHolder && ((CauseHolder) result).cause instanceof CancelException;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public Throwable cause() {
        if (result != null && result instanceof CauseHolder) {
            return ((CauseHolder) result).cause;
        }
        return null;
    }

    private void notifyListeners() {
        for (FutureListener<V> l : listeners) {
            notifyListener(l);
        }
    }

    private void notifyListener(FutureListener<V> l) {
        try {
            l.operationComplete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // 超时等待执行结果
        if (await(timeout, unit)) {
            Throwable cause = cause();
            // 没有发生异常，异步操作正常结束
            if (cause == null) {
                return getNow();
            }
            // 异步操作被取消了
            if (cause instanceof CancelException) {
                throw new ExecutionException(cause);
            }else {
                // 其他异常
                throw new ExecutionException(cause);
            }
        }
        // 时间到了异步操作还没有结束, 抛出超时异常
        throw new TimeoutException();
    }


    public ExtensionFuture<V> setFailure(Throwable cause) {
        if (setFailure0(cause)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    private boolean setFailure0(Throwable cause) {
        if (isDone()) {
            return false;
        }
        // 锁对象
        synchronized (this) {
            if (isDone()) {
                return false;
            }
            result = new CauseHolder(cause);
            notifyAll();
        }

        return true;
    }

    public ExtensionFuture<V> setSuccess(Object result) {
        // 设置成功后通知监听器
        if (setSuccess0(result)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    private boolean setSuccess0(Object result) {
        if (isDone()) {
            return false;
        }

        // 将该对象锁住
        synchronized (this) {
            if (isDone()) {
                return false;
            }
            // 异步操作正常执行完毕的结果是null
            if (result == null) {
                this.result = RESULT_NULL_FLAG;
            } else {
                this.result = result;
            }
            this.notifyAll();
            //notifyAll();
        }
        return true;
    }
}
