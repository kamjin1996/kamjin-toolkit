package com.kamjin.toolkit.db.crypt.mybatisplus.builder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodDecryptResolver;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodEncryptResolver;
import com.kamjin.toolkit.db.crypt.core.resolver.SimpleMethodEncryptResolver;
import com.kamjin.toolkit.db.crypt.mybatis.builder.MybatisMethodCryptMetadataBuilder;

import java.lang.reflect.Method;

/**
 * Mybatis Plus的MethodCryptMetadata 的建造者
 *
 * @author kamjin1996
 */
public class MybatisPlusMethodCryptMetadataBuilder extends MybatisMethodCryptMetadataBuilder {

    public MybatisPlusMethodCryptMetadataBuilder(Method runningMethod) {
        super(runningMethod);
    }

    @Override
    protected MethodEncryptResolver buildEncryptResolver(Method m) {
        if (BaseMapper.class.isAssignableFrom(m.getDeclaringClass())) {
            return new SimpleMethodEncryptResolver();
        }
        return super.buildEncryptResolver(m);
    }

    @Override
    protected MethodDecryptResolver buildDecryptResolver(Method m) {
        return super.buildDecryptResolver(m);
    }
}
