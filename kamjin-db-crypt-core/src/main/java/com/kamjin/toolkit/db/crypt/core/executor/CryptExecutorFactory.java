package com.kamjin.toolkit.db.crypt.core.executor;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.exception.DbCryptRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 加解密执行者工厂类
 *
 * @author kamjin1996
 */
public class CryptExecutorFactory {

    /**
     * CryptExecutor.class和对象的映射
     */
    private static final Map<Class<? extends CryptExecutor>, CryptExecutor> EXECUTOR_MAPPING = new HashMap<>();

    /**
     * 默认Executor
     */
    private static DefaultCryptExecutor DEFAULT_CRYPT_EXECUTOR = null;

    public static void registry(CryptExecutor executor) {
        if (Objects.isNull(executor)) {
            throw new DbCryptRuntimeException("CryptExecutor not be null");
        }
        if (executor instanceof DefaultCryptExecutor) {
            DEFAULT_CRYPT_EXECUTOR = (DefaultCryptExecutor) executor;
        }
        EXECUTOR_MAPPING.put(executor.getClass(), executor);
    }

    /**
     * 根据cryptField中不同的配置
     *
     * @param cryptField 加密的filed注解
     * @return CryptExecutor 加解密执行器
     */
    public static CryptExecutor getTypeHandler(CryptField cryptField) {
        if (cryptField.value() == DefaultCryptExecutor.class) { //快速判断 无需从map中取
            return DEFAULT_CRYPT_EXECUTOR;
        }

        CryptExecutor executor = EXECUTOR_MAPPING.get(cryptField.value());
        if (Objects.isNull(executor)) {
            throw new DbCryptRuntimeException("not found registered cryptExecutor bean: [" + cryptField.value() + "] please registry");
        }
        return executor;
    }
}
