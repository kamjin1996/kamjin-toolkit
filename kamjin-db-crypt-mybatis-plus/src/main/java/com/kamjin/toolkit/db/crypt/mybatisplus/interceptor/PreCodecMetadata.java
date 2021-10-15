package com.kamjin.toolkit.db.crypt.mybatisplus.interceptor;

import com.kamjin.toolkit.db.crypt.core.annotation.CryptField;
import org.apache.ibatis.mapping.BoundSql;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author kam
 * @since 2021/4/9
 *
 * <p>
 * 预加解密的元数据
 * </p>
 */
public class PreCodecMetadata {

    /**
     * dao的statementId
     */
    private String daoStatementId;

    /**
     * 执行的dao方法
     */
    private Method daoRunningMethod;

    /**
     * 当前加密字段所在的class
     */
    private Class<?> needCryptColumnInCls;

    /**
     * 需要加密的表字段列表 key为column value为CryptField
     */
    private Map<String, CryptField> needCryptColumns = new HashMap<>();

    public PreCodecMetadata() {
    }

    public String getDaoStatementId() {
        return daoStatementId;
    }

    public void setDaoStatementId(String daoStatementId) {
        this.daoStatementId = daoStatementId;
    }

    public Method getDaoRunningMethod() {
        return daoRunningMethod;
    }

    public void setDaoRunningMethod(Method daoRunningMethod) {
        this.daoRunningMethod = daoRunningMethod;
    }

    public Class<?> getNeedCryptColumnInCls() {
        return needCryptColumnInCls;
    }

    public void setNeedCryptColumnInCls(Class<?> needCryptColumnInCls) {
        this.needCryptColumnInCls = needCryptColumnInCls;
    }

    public Map<String, CryptField> getNeedCryptColumns() {
        return needCryptColumns;
    }

    public void setNeedCryptColumns(Map<String, CryptField> needCryptColumns) {
        this.needCryptColumns = needCryptColumns;
    }

    /**
     * 推断出statementId和sql语句的hashcode获取唯一key
     * 当前key构成: statementId + boundSql中sql的hashCode
     *
     * @param boundSql 当前sql对象
     * @return 当前statement结合参数的唯一的key
     */
    public String deduceStatementUniqueKey(BoundSql boundSql) {
        return daoStatementId + boundSql.getSql().hashCode();
    }
}
