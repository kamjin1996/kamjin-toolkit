package com.kamjin.toolkit.db.crypt.core.handler;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 加解密字段值处理
 * 注意：加解密只支持string，无论加密前是什么类型，加密后的内容只能以string进行存储
 * 所以需要加密的字段，必须要是string类型才可以
 * </p>
 */
public interface CodecFieldValueHandler {

    /**
     * 加密
     *
     * @param fieldValue
     * @return
     */
    String encrypt(String fieldValue);

    /**
     * 解密
     *
     * @param fieldValue
     * @return
     */
    String decrypt(String fieldValue);

}
