package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;

/**
 * 加解密处理抽象类
 *
 * @author kamjin1996
 */
public interface CryptHandler<T> {

    /**
     * 元素加密处理
     *
     * @param param
     * @param cryptField
     * @return
     */
    Object encrypt(T param, CryptField cryptField);

    /**
     * 元素解密处理
     *
     * @param param
     * @param cryptField
     * @return
     */
    Object decrypt(T param, CryptField cryptField);
}
