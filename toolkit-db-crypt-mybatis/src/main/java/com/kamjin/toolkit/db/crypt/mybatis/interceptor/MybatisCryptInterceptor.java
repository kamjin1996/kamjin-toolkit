package com.kamjin.toolkit.db.crypt.mybatis.interceptor;

import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;
import com.kamjin.toolkit.db.crypt.core.bean.KeyGenerateReference;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodCryptMetadata;
import com.kamjin.toolkit.db.crypt.core.util.CryptHelper;
import com.kamjin.toolkit.db.crypt.mybatis.builder.MybatisMethodCryptMetadataBuilder;
import com.kamjin.toolkit.db.crypt.mybatis.util.StatementUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加解密插件
 *
 * @author kamjin1996
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
                        BoundSql.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisCryptInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisCryptInterceptor.class);

    private boolean switchCrypt = false;

    private String primaryKeyName;

    /**
     * 需加解密处理方法的信息
     */
    private static final ConcurrentHashMap<String, MethodCryptMetadata> METHOD_ENCRYPT_MAP = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (isSwitchCrypt()) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            Method runningMethod = StatementUtil.deduceMethodById(mappedStatement.getId());
            MethodCryptMetadata methodCryptMetadata = getCachedMethodCryptMetaData(mappedStatement, runningMethod);
            args[1] = methodCryptMetadata.encrypt(args[1]);
            Object returnValue = invocation.proceed();
            this.ifInsertReturnId(mappedStatement.getSqlCommandType());
            return methodCryptMetadata.decrypt(returnValue);
        } else {
            return invocation.proceed();
        }
    }

    /**
     * 如果是插入，则将id返回
     *
     * @param currentCommandType 本次的sqlCommand类型
     */
    private void ifInsertReturnId(SqlCommandType currentCommandType) {
        boolean isInsert = Objects.equals("INSERT", currentCommandType.name());
        if (isInsert) {
            try {
                KeyGenerateReference reference = CryptHelper.getKeyGenerateReference();
                this.returnIdToSourceBean(reference);
            } catch (Exception e) {
                log.error("将生成的key放到新的bean错误：", e);
            } finally {
                CryptHelper.cleanKeyGenerateReference();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        DbcryptProperties.getInstance().ifPresent(x -> {
            this.primaryKeyName = x.getPrimaryKeyName();
        });
    }

    private MethodCryptMetadata getCachedMethodCryptMetaData(MappedStatement mappedStatement, Method runningMethod) {
        return METHOD_ENCRYPT_MAP.computeIfAbsent(mappedStatement.getId(),
                id -> new MybatisMethodCryptMetadataBuilder(runningMethod).build());
    }

    private boolean isSwitchCrypt() {
        DbcryptProperties.getInstance().ifPresent(x -> {
            this.switchCrypt = x.getEnable();
        });
        return switchCrypt;
    }

    /**
     * 修复selectKey无法赋值给源对象(源对象被clone,因为需要避免重复加密)
     *
     * @param reference keyGen的引用
     * @throws IllegalAccessException 权限异常
     * @throws NoSuchFieldException   没有对应属性异常
     */
    private void returnIdToSourceBean(KeyGenerateReference reference) throws IllegalAccessException, NoSuchFieldException {
        if (reference != null) {
            Object sourceObj = reference.getOriginPojo();
            Object cloneObj = reference.getClonePojo();

            Field sourceObjFieldId = sourceObj.getClass().getDeclaredField(primaryKeyName);
            sourceObjFieldId.setAccessible(true);
            Field cloneObjFieldId = cloneObj.getClass().getDeclaredField(primaryKeyName);
            cloneObjFieldId.setAccessible(true);
            Object cloneObjFieldIdVal = cloneObjFieldId.get(cloneObj);
            if (Objects.nonNull(cloneObjFieldIdVal)) {
                sourceObjFieldId.set(sourceObj, cloneObjFieldIdVal);
            }
        }
    }
}
