package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 抽像的加解密字段值处理器
 * </p>
 */
public abstract class AbstractCodecFiledValueHandler implements CodecFieldValueHandler {

    private boolean enable = false;

    @Override
    public String encrypt(String sSrc) {
        return isEnable() ? doEncrypt(sSrc) : sSrc;
    }

    /**
     * 加密实现
     *
     * @param sSrc
     * @return
     */
    protected abstract String doEncrypt(String sSrc);

    @Override
    public String decrypt(String sSrc) {
        return isEnable() ? doDecrypt(sSrc) : sSrc;
    }

    /**
     * 解密实现
     *
     * @param sSrc
     * @return
     */
    protected abstract String doDecrypt(String sSrc);

    private boolean isEnable() {
        return assignment(x -> enable = x.getEnable()) && enable;
    }

    private boolean assignment(Consumer<? super DbcryptProperties> consumer) {
        Optional<DbcryptProperties> dbcrypt = DbcryptProperties.getInstance();
        dbcrypt.ifPresent(consumer);
        return dbcrypt.isPresent();
    }

}
