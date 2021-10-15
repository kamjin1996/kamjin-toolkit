package com.kamjin.toolkit.db.crypt.mybatis.util;

import java.lang.reflect.Method;

/**
 * @author kam
 * @since 2021/4/10
 *
 * <p>
 * statement工具
 * </p>
 */
public final class StatementUtil {

    /**
     * 根据statementId获取运行的方法
     *
     * @param statementId statementId
     * @return 函数
     * @throws ClassNotFoundException 未找到class
     */
    public static Method deduceMethodById(String statementId) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(statementId.substring(0, statementId.lastIndexOf(".")));
        final String methodName = statementId.substring(statementId.lastIndexOf(".") + 1);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
