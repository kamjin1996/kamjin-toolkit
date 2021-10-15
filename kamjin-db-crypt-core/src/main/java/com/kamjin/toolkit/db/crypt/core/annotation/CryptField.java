package com.kamjin.toolkit.db.crypt.core.annotation;

import com.kamjin.toolkit.db.crypt.core.executor.DefaultCryptExecutor;
import com.kamjin.toolkit.db.crypt.core.executor.CryptExecutor;

import java.lang.annotation.*;

/**
 * 加解密注解
 *
 * @author kamjin1996
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CryptField {

    Class<? extends CryptExecutor> value() default DefaultCryptExecutor.class;

    boolean encrypt() default true;

    boolean decrypt() default true;
}
