package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 抽像的加解密字段值处理器
 * </p>
 */
public abstract class AbstractCodecFiledValueHandler implements CodecFieldValueHandler {

    private DbcryptProperties dbcryptProperties;

    public AbstractCodecFiledValueHandler() {

    }

    public AbstractCodecFiledValueHandler(DbcryptProperties dbcryptProperties) {
        this.dbcryptProperties = dbcryptProperties;
    }

    public DbcryptProperties getDbcryptProperties() {
        return dbcryptProperties;
    }

    public void setDbcryptProperties(DbcryptProperties dbcryptProperties) {
        this.dbcryptProperties = dbcryptProperties;
    }

    @Override
    public String encrypt(String sSrc) {
        return isEnable() ? doEncrypt(sSrc) : sSrc;
    }

    /**
     * 加密实现
     *
     * @param sSrc 加密前字符串
     * @return 加密后结果
     */
    protected abstract String doEncrypt(String sSrc);

    @Override
    public String decrypt(String sSrc) {
        return isEnable() ? doDecrypt(sSrc) : sSrc;
    }

    /**
     * 解密实现
     *
     * @param sSrc 解密前字符串
     * @return 解密后结果
     */
    protected abstract String doDecrypt(String sSrc);

    private boolean isEnable() {
        return this.dbcryptProperties.getEnable();
    }

}
