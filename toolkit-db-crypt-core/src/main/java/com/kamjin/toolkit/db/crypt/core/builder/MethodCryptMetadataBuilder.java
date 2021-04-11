package com.kamjin.toolkit.db.crypt.core.builder;

import com.kamjin.toolkit.db.crypt.core.resolver.MethodCryptMetadata;

/**
 * @author kam
 * @since 2021/4/8
 *
 * <p>
 * 函数加解密元数据创建者
 * </p>
 */
public interface MethodCryptMetadataBuilder {

    /**
     * 构建函数加解密处理元数据
     *
     * @return
     */
    MethodCryptMetadata build();
}
