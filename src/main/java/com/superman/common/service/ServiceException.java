package com.superman.common.service;

/**
 * Define Super.Sun.
 * <p>Created with IntelliJ IDEA on 2016/6/9.</p>
 * Service层公用的Exception, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * @author Super.Sun
 * @version 1.0
 */
public class ServiceException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
