package com.kamjin.toolkit.db.crypt.core.bean;

import com.kamjin.toolkit.db.crypt.core.enums.AesEnum;

import java.util.Objects;
import java.util.Optional;

/**
 * 数据库加密配置属性映射
 *
 * @author kamjin1996
 */
public class DbcryptProperties {

    private static DbcryptProperties INSTANCE = new DbcryptProperties();

    private AesEnum aes;

    private String secretkey;

    private Boolean enable;

    private String primaryKeyName;

    private DbcryptProperties() {
    }

    public DbcryptProperties(AesEnum aes, String secretkey) {
        this(aes, secretkey, true, "id");
    }

    public DbcryptProperties(AesEnum aes, String secretkey, Boolean enable, String primaryKeyName) {
        check(secretkey, enable, aes);
        this.aes = aes;
        this.secretkey = secretkey;
        this.enable = enable;
        this.primaryKeyName = primaryKeyName;
        INSTANCE = this;
    }

    public static Optional<DbcryptProperties> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    public AesEnum getAes() {
        return aes;
    }

    public void setAes(String aes) {
        this.aes = AesEnum.byName(aes);
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

    private static void check(String secretkey, Boolean enable, AesEnum aes) {
        if (Objects.isNull(secretkey)) {
            throw new IllegalArgumentException("secretkey not be null");
        }
        if (Objects.isNull(enable)) {
            throw new IllegalArgumentException("enable not be null");
        }
        if (Objects.isNull(aes)) {
            throw new IllegalArgumentException("aes not be null");
        }
    }


}
