package com.kamjin.toolkit.db.crypt.core.resolver;

import java.util.Objects;

/**
 * 方法加解密 元数据
 *
 * @author kamjin1996
 */
public class MethodCryptMetadata {

    /**
     * 方法加密处理者
     */
    public MethodEncryptResolver methodEncryptResolver;

    /**
     * 方法解密处理者
     */
    public MethodDecryptResolver methodDecryptResolver;

    public MethodEncryptResolver getMethodEncryptResolver() {
        return methodEncryptResolver;
    }

    public void setMethodEncryptResolver(MethodEncryptResolver methodEncryptResolver) {
        this.methodEncryptResolver = methodEncryptResolver;
    }

    public MethodDecryptResolver getMethodDecryptResolver() {
        return methodDecryptResolver;
    }

    public void setMethodDecryptResolver(MethodDecryptResolver methodDecryptResolver) {
        this.methodDecryptResolver = methodDecryptResolver;
    }

    public Object encrypt(Object object) {
        if (Objects.isNull(object)) {
            return object;
        }
        return methodEncryptResolver.processEncrypt(object);
    }

    public Object decrypt(Object object) {
        if (Objects.isNull(object)) {
            return object;
        }
        return methodDecryptResolver.processDecrypt(object);
    }
}
