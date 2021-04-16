package com.sedwt.fure.cause;

/**
 * @author : zhang yijun
 * @date : 2021/4/16 11:44
 * @description : TODO
 */

public class CauseHolder {
    public Throwable cause;
    public CauseHolder(Throwable cause) {
        this.cause = cause;
    }

    public CauseHolder() {
    }
}
