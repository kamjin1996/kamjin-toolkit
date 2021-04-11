package com.kamjin.toolkit.db.crypt.core.exception;

/**
 * 自定义运行时异常，避免太多异常链
 *
 * @author kamjin1996
 */
public class DbCryptRuntimeException extends RuntimeException {

    public DbCryptRuntimeException() {}

    public DbCryptRuntimeException(String message) {
        super(message);
    }

}
