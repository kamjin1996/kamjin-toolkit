package com.kamjin.toolkit.db.crypt.mybatisplus.interceptor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import com.kamjin.toolkit.db.crypt.core.resolver.MethodCryptMetadata;
import com.kamjin.toolkit.db.crypt.mybatis.builder.MybatisMethodCryptMetadataBuilder;
import com.kamjin.toolkit.db.crypt.mybatis.util.StatementUtil;
import com.kamjin.toolkit.db.crypt.mybatisplus.builder.MybatisPlusMethodCryptMetadataBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author kam
 * @since 2021/4/11
 *
 * <p>
 * statement元数据缓存管理
 * </p>
 */
public class StatementCryptMetadataCacheManager {

    /**
     * statement的预加密参数缓存
     */
    private static final ConcurrentHashMap<String, PreCodecMetadata> STATEMENT_PRE_CODEC_METADATA_CACHE = new ConcurrentHashMap<>(128);


    /**
     * statement的唯一key（@see PreCodecMetadata.deduceStatementUniqueKey()）和MP生成的字段占位符缓存
     */
    private static final ConcurrentHashMap<String, Map<String, CryptField>> STATEMENT_UNIQUE_KEY_PLACEHOLDERS_CACHE = new ConcurrentHashMap<>(128);

    /**
     * statement需加解密处理方法的信息
     */
    private static final ConcurrentHashMap<String, MethodCryptMetadata> STATEMENT_METHOD_ENCRYPT_MAP = new ConcurrentHashMap<>();

    /**
     * 获取缓存的statement预加密元信息
     *
     * @param statementId statementId
     * @return 预加密的参数元信息
     */
    public PreCodecMetadata getCachedStatementPreCodecMetadata(String statementId) {
        return STATEMENT_PRE_CODEC_METADATA_CACHE.computeIfAbsent(statementId,
                id -> {
                    try {
                        return this.obtainPreCodecMetadata(statementId);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    /**
     * 获取缓存的mp占位符列表
     *
     * @param wrapper  本次使用的mybatisPlus的wrapper对象
     * @param metadata 预加密的参数元信息
     * @param boundSql boundSql
     * @return mp参数占位符
     */
    public Map<String, CryptField> getCachedMethodMqPlaceHolders(Wrapper<?> wrapper, PreCodecMetadata metadata, BoundSql boundSql) {
        return STATEMENT_UNIQUE_KEY_PLACEHOLDERS_CACHE.computeIfAbsent(metadata.deduceStatementUniqueKey(boundSql),
                id -> this.resolverMpValuePlaceHolders(wrapper, metadata));
    }

    /**
     * 获取缓存的方法加密元数据
     *
     * @param statementId   statementId
     * @param runningMethod 运行中的函数
     * @return 函数加密元信息
     */
    public MethodCryptMetadata getCachedMethodCryptMetaData(String statementId, Method runningMethod) {
        return STATEMENT_METHOD_ENCRYPT_MAP.computeIfAbsent(statementId,
                id -> new MybatisPlusMethodCryptMetadataBuilder(runningMethod).build());
    }

    /**
     * 根据wrapper和加密元数据获取mp值下标列表
     *
     * @param wrapper  本次使用的mybatisPlus的wrapper对象
     * @param metadata 预加密的参数元信息
     * @return mp参数占位符
     */
    private Map<String, CryptField> resolverMpValuePlaceHolders(Wrapper<?> wrapper, PreCodecMetadata metadata) {
        //查看类型 如果是lambadaQueryWrapper 需要解析SQLSegment，如果是lambadaUpdateWrapper 需要解析SQLSegment和setSql
        MergeSegments expression = wrapper.getExpression();
        if (Objects.isNull(expression)) {
            //没有参数就是EmptyWrapper，获取的expression为空
            return new HashMap<>();
        }

        List<String> sqlSegmentOrSetSqls = new ArrayList<>();

        sqlSegmentOrSetSqls.add(expression.getSqlSegment());
        if (wrapper instanceof LambdaUpdateWrapper) {
            sqlSegmentOrSetSqls.add(wrapper.getSqlSet());
        }

        //过滤掉null值
        sqlSegmentOrSetSqls = sqlSegmentOrSetSqls.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return this.parseSqlSegmentOrSetSqlGainIndex(sqlSegmentOrSetSqls, metadata);
    }

    /**
     * 解析sqlSegment获取需加密字段mq映射的key
     *
     * @param sqlSegmentOrSetSqls     sqlSegment或者setSql的列表
     * @param beforeInterceptMetadata 加密的参数元信息
     * @return mp参数占位符
     */
    private Map<String, CryptField> parseSqlSegmentOrSetSqlGainIndex(List<String> sqlSegmentOrSetSqls, PreCodecMetadata beforeInterceptMetadata) {
        Map<String, CryptField> mqValuePlaceholders = new HashMap<>();
        // 输入加解密的表字段（非变量名，需要取tableFiled中的value）列表 获取下标列表
        Map<String, CryptField> needCryptColumns = beforeInterceptMetadata.getNeedCryptColumns();
        needCryptColumns.entrySet().forEach(entry -> {
            sqlSegmentOrSetSqls.forEach(sqlSegmentOrSetSql -> {
                if (sqlSegmentOrSetSql.contains(entry.getKey())) {
                    //去除空格
                    sqlSegmentOrSetSql = sqlSegmentOrSetSql.replaceAll(" ", "");

                    //例子：MPGENVAL1 参考Constants.WRAPPER_PARAM_FORMAT
                    String sub = sqlSegmentOrSetSql.substring(sqlSegmentOrSetSql.indexOf(entry.getKey()) + (entry.getKey() + "=#{ew.paramNameValuePairs.").length());
                    String mpgenvalIndex = sub.substring(sub.indexOf(Constants.WRAPPER_PARAM), sub.indexOf("}"));
                    if (StringUtils.isNotBlank(mpgenvalIndex)) {
                        mqValuePlaceholders.put(mpgenvalIndex, entry.getValue());
                    }
                }
            });

        });
        return mqValuePlaceholders;
    }

    /**
     * 获取预加密的元素列表
     *
     * @param statementId statementId
     * @return 预加密的参数元信息
     * @throws ClassNotFoundException 未找到class异常
     */
    @SuppressWarnings("rawtypes")
    private PreCodecMetadata obtainPreCodecMetadata(String statementId) throws ClassNotFoundException {
        //获取当前对应数据操作的pojo
        final Class<?> clazz = Class.forName(statementId.substring(0, statementId.lastIndexOf(".")));
        Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.getTypeVariableMap(clazz);
        Optional<Type> first = typeVariableMap.values().stream().findFirst();
        if (!first.isPresent()) {
            return null;
        }

        Type type = first.get();
        Class<?> aClass = Class.forName(type.getTypeName());

        //获取所有私有变量
        Field[] declaredFields = aClass.getDeclaredFields();

        List<Field> needCryptFields = Arrays.stream(declaredFields)
                .filter(x -> x.getAnnotation(CryptField.class) != null).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(needCryptFields)) {
            return null;
        }

        PreCodecMetadata metadata = new PreCodecMetadata();
        metadata.setDaoStatementId(statementId);
        Method runningMethod = StatementUtil.deduceMethodById(statementId);
        metadata.setDaoRunningMethod(runningMethod);
        metadata.setNeedCryptColumnInCls(aClass);

        for (Field field : needCryptFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);

            //获取真实column名称
            TableField annotation = field.getAnnotation(TableField.class);
            String cryptColumn = Objects.nonNull(annotation) ? annotation.value() : field.getName();

            metadata.getNeedCryptColumns().put(cryptColumn, cryptField);
        }
        return metadata;
    }
}
