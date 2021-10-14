package com.kamjin.toolkit.db.crypt.mybatisplus.interceptor;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;
import com.kamjin.toolkit.db.crypt.core.exception.DbCryptRuntimeException;
import com.kamjin.toolkit.db.crypt.core.executor.CryptExecutor;
import com.kamjin.toolkit.db.crypt.core.executor.CryptExecutorFactory;
import com.kamjin.toolkit.db.crypt.core.executor.DefaultCryptExecutor;
import com.kamjin.toolkit.db.crypt.core.handler.CodecFieldValueHandler;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodCryptMetadata;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodDecryptResolver;
import com.kamjin.toolkit.db.crypt.core.resolver.SimpleMethodDecryptResolver;
import com.kamjin.toolkit.db.crypt.core.util.CryptHelper;
import com.kamjin.toolkit.db.crypt.mybatis.interceptor.MybatisCryptInterceptor;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author kam
 * @since 2021/4/9
 *
 * <p>
 * mybatisPlus加密插件
 * 1.确定需要加密参数的fieldList，通过线程副本传递下去
 * 注意：勿与mybatis加解密插件同时注册 {@link MybatisCryptInterceptor}
 * </p>
 */
@Intercepts(value = {
        @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
                        BoundSql.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisPlusCryptInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusCryptInterceptor.class);

    /**
     * 数据库加密属性
     */
    private DbcryptProperties dbcryptProperties;

    /**
     * 是否注册了mp的内部分页插件
     */
    private boolean registeredPaginationInterceptor = false;

    /**
     * 唯一结果处理
     */
    private MethodDecryptResolver simpleResultDecryptResolver;

    /**
     * statement加密元数据缓存管理器
     */
    private StatementCryptMetadataCacheManager metadataCacheManager;

    /**
     * 预加密的参数上下文信息
     */
    private static final ThreadLocal<PreCodecMetadata> CODEC_METADATA_THREAD_LOCAL = new ThreadLocal<>();

    public MybatisPlusCryptInterceptor(DbcryptProperties dbcryptProperties, CryptExecutor cryptExecutor) {
        if (Objects.isNull(dbcryptProperties)) {
            throw new DbCryptRuntimeException("dbcryptProperties must not null");
        }
        this.dbcryptProperties = dbcryptProperties;
        this.simpleResultDecryptResolver = new SimpleMethodDecryptResolver();
        this.metadataCacheManager = new StatementCryptMetadataCacheManager();

        CryptExecutorFactory.registry(cryptExecutor);//注册executor
    }

    private MybatisPlusCryptInterceptor() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!isSwitchCrypt()) {
            return invocation.proceed();
        }

        //根据拦截的参数来确定执行哪个处理
        if (invocation.getTarget() instanceof Executor) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            //寻找需加密字段的标识列表 入参标识列表 和 出参标识标识 传递下去，在随后的处理器做加密处理
            PreCodecMetadata metadata = this.metadataCacheManager.getCachedStatementPreCodecMetadata(mappedStatement.getId());
            CODEC_METADATA_THREAD_LOCAL.set(metadata);
            Object result = invocation.proceed();

            CryptHelper.cleanKeyGenerateReference();
            //结果解密处理
            return this.decryptResult(result);
        }

        if (invocation.getTarget() instanceof ParameterHandler) {
            PreCodecMetadata metadata = CODEC_METADATA_THREAD_LOCAL.get();
            if (Objects.isNull(metadata)) {
                return invocation.proceed();
            }
            Object result = this.invokeSetParameter(invocation, metadata);//结果为空
            CODEC_METADATA_THREAD_LOCAL.remove();
            return result;
        }
        return null;//不会执行
    }

    private Object decryptResult(Object result) {
        return this.simpleResultDecryptResolver.processDecrypt(result);
    }

    /**
     * 执行setParameter
     *
     * @param invocation invocation
     * @param metadata   预加密的元数据
     * @return setParameter执行结果
     * @throws Exception 其他异常
     */
    @SuppressWarnings("rawtypes")
    private Object invokeSetParameter(Invocation invocation, PreCodecMetadata metadata) throws Exception {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];

        // 反射获取参数对象
        Field parameterField = parameterHandler.getClass().getDeclaredField("parameterObject");
        parameterField.setAccessible(true);
        Object parameterObject = parameterField.get(parameterHandler);

        // 反射获取 BoundSql 对象，此对象包含生成的sql和sql的参数map映射
        Field boundSqlField = parameterHandler.getClass().getDeclaredField("boundSql");
        boundSqlField.setAccessible(true);
        BoundSql boundSql = (BoundSql) boundSqlField.get(parameterHandler);

        //如果是paramMap 且ew参数不为空 按照mybatisPlus的wrapper解析处理
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper) ((MapperMethod.ParamMap) parameterObject).get(Constants.WRAPPER);
        } catch (BindingException | ClassCastException ignored) {
        }

        if (Objects.nonNull(wrapper)) {
            Map<String, CryptField> cachedMethodMqPlaceHolders = this.metadataCacheManager.getCachedMethodMqPlaceHolders(wrapper, metadata, boundSql);
            this.encryptParam(cachedMethodMqPlaceHolders, parameterObject);
        } else {
            MethodCryptMetadata metaData = this.metadataCacheManager.getCachedMethodCryptMetaData(metadata.getDaoStatementId(), metadata.getDaoRunningMethod());
            parameterObject = metaData.encrypt(parameterObject);
        }

        // 改写的参数设置到原parameterHandler对象
        parameterField.set(parameterHandler, parameterObject);
        parameterHandler.setParameters(ps);
        return invocation.proceed();
    }

    /**
     * 在paramObject里加密数据
     *
     * @param mqValuePlaceholders mybatisplus的值占位符
     * @param paramObject         参数对象
     */
    @SuppressWarnings("all")
    private void encryptParam(Map<String, CryptField> mqValuePlaceholders, Object paramObject) {
        if (CollectionUtils.isEmpty(mqValuePlaceholders)) {
            return;
        }
        Map ew = ((AbstractWrapper) ((MapperMethod.ParamMap) paramObject).get(Constants.WRAPPER)).getParamNameValuePairs();
        for (Map.Entry<String, CryptField> mqValuePlaceholder : mqValuePlaceholders.entrySet()) {
            String oldVal = String.valueOf(ew.get(mqValuePlaceholder.getKey()));
            ew.put(mqValuePlaceholder.getKey(), CryptExecutorFactory.getTypeHandler(mqValuePlaceholder.getValue()).encrypt(oldVal));
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private boolean isSwitchCrypt() {
        return this.dbcryptProperties.getEnable();
    }

    /**
     * @param registeredPaginationInterceptor
     */
    public void setRegisteredPaginationInterceptor(boolean registeredPaginationInterceptor) {
        this.registeredPaginationInterceptor = registeredPaginationInterceptor;
    }

}
