package com.kamjin.toolkit.db.crypt.core.handler;

import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

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

    private DefaultAESCodecFieldValueHandler() {
    }

    public DefaultAESCodecFieldValueHandler(DbcryptProperties dbcryptProperties) {
        super(dbcryptProperties);
    }

    private static final String CRYPT_WAY = "AES";
    private static final String ALGORITHM_MODE_COMPLEMENT = "AES/ECB/PKCS5Padding"; // 算法/模式/补码方式
    private static final String BYTE_CONTROL = "utf-8";
    private static final String SECURE_RANDOM_INSTANCE_NAME = "SHA1PRNG";

    @Override
    protected String doEncrypt(String sSrc) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
            secureRandom.setSeed(this.getDbcryptProperties().getSecretkey().getBytes());
            kgen.init(this.getDbcryptProperties().getAes().getStandSupport(), secureRandom);

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

    @Override
    protected String doDecrypt(String sSrc) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
            secureRandom.setSeed(this.getDbcryptProperties().getSecretkey().getBytes());

            // kgen.init(checkOrGetDbCryptSupport(), new SecureRandom(sKey.getBytes()));
            kgen.init(this.getDbcryptProperties().getAes().getStandSupport(), secureRandom);

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

}
