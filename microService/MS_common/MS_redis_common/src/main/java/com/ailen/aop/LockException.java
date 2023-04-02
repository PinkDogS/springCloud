package com.ailen.aop;

/**
 * 分布式锁异常
 *
 * @author anshare
 * @date
 */
public class LockException extends RuntimeException {

    private static final long serialVersionUID = -7685478031686622211L;

    public LockException(String message) {
        super(message);
    }
}
