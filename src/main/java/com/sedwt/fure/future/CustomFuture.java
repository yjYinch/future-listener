package com.sedwt.fure.future;

/**
 * @author : zhang yijun
 * @date : 2021/4/16 13:47
 * @description : TODO
 */

public class CustomFuture<V> extends AbstractFuture<V>{

    @Override
    public ExtensionFuture<V> setFailure(Throwable cause) {
        return super.setFailure(cause);
    }

    @Override
    public ExtensionFuture<V> setSuccess(Object result) {
        return super.setSuccess(result);
    }
}
