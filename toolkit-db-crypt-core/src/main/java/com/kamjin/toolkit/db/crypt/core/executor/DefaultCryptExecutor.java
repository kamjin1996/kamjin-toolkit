package com.kamjin.toolkit.db.crypt.core.executor;

import com.kamjin.toolkit.db.crypt.core.handler.CodecFieldValueHandler;

/**
 * 默认加解密执行者
 *
 * @author kamjin1996
 */
public class DefaultCryptExecutor implements CryptExecutor {

    private CodecFieldValueHandler codecFieldValueHandler;

    public void setCodecFieldHandler(CodecFieldValueHandler codecFieldValueHandler) {
        this.codecFieldValueHandler = codecFieldValueHandler;
    }

    public DefaultCryptExecutor(CodecFieldValueHandler codecFieldValueHandler) {
        this.codecFieldValueHandler = codecFieldValueHandler;
    }

    @Override
    public String encrypt(String str) {
        return codecFieldValueHandler.encrypt(str);
    }

    @Override
    public String decrypt(String str) {
        return codecFieldValueHandler.decrypt(str);
    }
}
