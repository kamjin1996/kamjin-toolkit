package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.enums.AesEnum;
import com.kamjin.toolkit.db.crypt.core.exception.DbCryptRuntimeException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 默认的加解密字段处理者 采取AES加解密
 * </p>
 */
public class DefaultAESCodecFieldValueHandler extends AbstractCodecFiledValueHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultAESCodecFieldValueHandler.class);

    private String secretKey = "123456789012345678901234";

    private AesEnum aesEnum = AesEnum.AES192;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public AesEnum getAesEnum() {
        return aesEnum;
    }

    public void setAesEnum(AesEnum aesEnum) {
        this.aesEnum = aesEnum;
    }

    private static final String CRYPT_WAY = "AES";
    private static final String ALGORITHM_MODE_COMPLEMENT = "AES/ECB/PKCS5Padding"; // 算法/模式/补码方式
    private static final String BYTE_CONTROL = "utf-8";
    private static final String SECURE_RANDOM_INSTANCE_NAME = "SHA1PRNG";

    /**
     * 加密
     *
     * @param sSrc
     * @return
     */
    @Override
    protected String doEncrypt(String sSrc) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
            secureRandom.setSeed(checkOrGetDbCryptSecretKey().getBytes());
            kgen.init(checkOrGetDbCryptSupport(), secureRandom);

            byte[] encodeFormat = kgen.generateKey().getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(encodeFormat, CRYPT_WAY);

            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(BYTE_CONTROL));

            // 此处使用BASE64做转码功能，能起到2次加密的作用。
            return new Base64().encodeToString(encrypted);
        } catch (Exception e) {
            log.warn("encrypt str failed:[{}],rollback to source str:[{}]", e.getMessage(), sSrc);
        }
        return sSrc;
    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     */
    @Override
    protected String doDecrypt(String sSrc) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
            secureRandom.setSeed(checkOrGetDbCryptSecretKey().getBytes());

            // kgen.init(checkOrGetDbCryptSupport(), new SecureRandom(sKey.getBytes()));
            kgen.init(checkOrGetDbCryptSupport(), secureRandom);

            SecretKeySpec secretKeySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), CRYPT_WAY);
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] original = cipher.doFinal(new Base64().decode(sSrc));
            return new String(original, BYTE_CONTROL);
        } catch (Exception e) {
            log.warn("decrypt str failed:[{}],rollback to source str:[{}]", e.getMessage(), sSrc);
        }
        return sSrc;
    }

    private String checkOrGetDbCryptSecretKey() {
        String secretkey = getSecretKey();
        checkKey(secretkey);
        return secretkey;
    }

    private int checkOrGetDbCryptSupport() {
        return checkOrGetAesEnum().getStandSupport();
    }

    private AesEnum checkOrGetAesEnum() {
        AesEnum aesEnum = getAesEnum();
        if (Objects.isNull(aesEnum)) {
            throw new DbCryptRuntimeException("dbcrypt initialized faild");
        }
        return aesEnum;
    }

    /**
     * 检查SecretKey
     */
    private void checkKey(String sKey) {
        if (Objects.isNull(sKey) || StringUtils.isBlank(sKey)) {
            throw new DbCryptRuntimeException("secretkey not blank");
        }
        if (sKey.length() != checkOrGetAesEnum().getSecretKeyLength()) {
            throw new DbCryptRuntimeException("secretkey length not support,[" + sKey.length() + "]");
        }
    }
}
