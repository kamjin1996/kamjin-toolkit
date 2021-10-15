package com.kamjin.toolkit.db.crypt.core.bean;

import com.kamjin.toolkit.db.crypt.core.enums.AesEnum;

/**
 * 数据库加密配置属性映射
 *
 * @author kamjin1996
 */
public class DbcryptProperties {

    private AesEnum aes;

    private String secretkey;

    private Boolean enable;

    private String primaryKeyName;

    public DbcryptProperties() {
        aes = AesEnum.AES192;
        enable = Boolean.TRUE;
        primaryKeyName = "id";
        secretkey = "123456789012345678901234";
    }

    public DbcryptProperties(AesEnum aes, String secretkey, Boolean enable, String primaryKeyName) {
        this.aes = aes;
        this.secretkey = secretkey;
        this.enable = enable;
        this.primaryKeyName = primaryKeyName;
    }

    public AesEnum getAes() {
        return aes;
    }

    public void setAes(AesEnum aes) {
        this.aes = aes;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }
}
