package com.kamjin.toolkit.db.crypt.core.executor;

/**
 * 加解密执行者，可能是加密手机号码，可能是加密姓名等
 *
 * @author kamjin1996
 */
public interface CryptExecutor {

    /**
     * 加密
     *
     * @param str
     * @return
     */
    String encrypt(String str);

    /**
     * 解密
     *
     * @param str
     * @return
     */
    String decrypt(String str);
}
