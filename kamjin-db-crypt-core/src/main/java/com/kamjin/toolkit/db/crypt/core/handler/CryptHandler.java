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
     * @param param 含有需加密处理的参数
     * @param cryptField 加解密注解
     * @return 加密结果
     */
    Object encrypt(T param, CryptField cryptField);

    /**
     * 元素解密处理
     *
     * @param param 含有需解密处理的参数
     * @param cryptField 加解密注解
     * @return 解密结果
     */
    Object decrypt(T param, CryptField cryptField);
}
