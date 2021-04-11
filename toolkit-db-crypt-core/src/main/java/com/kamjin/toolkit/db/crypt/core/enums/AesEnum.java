package com.kamjin.toolkit.db.crypt.core.enums;

import com.kamjin.toolkit.db.crypt.core.exception.DbCryptRuntimeException;

import java.util.Objects;

/**
 * Aes算法枚举
 *
 * @author kamjin1996
 */
public enum AesEnum {

    /**
     * 标准与密钥长度、轮数
     */
    AES128("AES-128", 128, 16, 10),

    AES192("AES-192", 192, 24, 12),

    AES256("AES-256", 256, 32, 14);

    private final String standard;

    private final int standSupport;

    private final int secretKeyLength;

    private final int round;

    public String getStandard() {
        return standard;
    }

    public int getStandSupport() {
        return standSupport;
    }

    public int getSecretKeyLength() {
        return secretKeyLength;
    }

    public int getRound() {
        return round;
    }

    AesEnum(String standard, int standSupport, int secretKeyLength, int round) {
        this.standard = standard;
        this.standSupport = standSupport;
        this.secretKeyLength = secretKeyLength;
        this.round = round;
    }

    public static AesEnum byName(String aesName) {
        AesEnum[] values = AesEnum.values();
        for (AesEnum aes : values) {
            if (Objects.equals(aesName, aes.name())) {
                return aes;
            }
        }
        throw new DbCryptRuntimeException("AES Standard not found [" + aesName + "]");
    }
}
