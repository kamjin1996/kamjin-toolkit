package com.kamjin.toolkit.db.crypt.core.builder;

import com.kam.toolkit.db.crypt.core.resolver.*;
import com.kamjin.toolkit.db.crypt.core.resolver.*;

import java.lang.reflect.Method;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 抽象的元数据创建者
 * </p>
 */
public abstract class AbstractMethodCryptMetadataBuilder implements MethodCryptMetadataBuilder {

    protected static final MethodEncryptResolver EMPTY_ENCRYPT_RESOLVER = new EmptyMethodEncryptResolver();
    protected static final MethodDecryptResolver EMPTY_DECRYPT_RESOLVER = new EmptyMethodDecryptResolver();

    public AbstractMethodCryptMetadataBuilder(Method runningMethod) {
        this.runningMethod = runningMethod;
    }

    private final Method runningMethod;

    @Override
    public MethodCryptMetadata build() {
        MethodCryptMetadata metadata = new MethodCryptMetadata();
        metadata.setMethodEncryptResolver(buildEncryptResolver(runningMethod));
        metadata.setMethodDecryptResolver(buildDecryptResolver(runningMethod));
        return metadata;
    }

    /**
     * 构建解密处理器
     *
     * @param m
     * @return
     */
    protected abstract MethodDecryptResolver buildDecryptResolver(Method m);

    /**
     * 构建加密处理器
     *
     * @param m
     * @return
     */
    protected abstract MethodEncryptResolver buildEncryptResolver(Method m);

}
