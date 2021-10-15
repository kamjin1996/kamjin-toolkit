package com.kamjin.toolkit.db.crypt.mybatisplus.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author kam
 * @since 2021/10/15
 *
 * <p>
 * 更改指定参数值的插件
 * </p>
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ModifyParamValueInterceptor implements Interceptor {

    /**
     * 参数更改者集合
     */
    private final List<Consumer<MetaObject>> paramChangers = new ArrayList<>();

    public ModifyParamValueInterceptor() {
    }

    /**
     * 获取所有的参数更改者
     *
     * @return
     */
    public List<Consumer<MetaObject>> getParamChangers() {
        return Collections.unmodifiableList(paramChangers);
    }

    /**
     * 添加参数更改者
     *
     * @param paramChanger 参数更改者
     */
    public void addParamChanger(Consumer<MetaObject> paramChanger) {
        this.paramChangers.add(paramChanger);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (CollectionUtils.isEmpty(paramChangers)) {
            return invocation.proceed();
        }

        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        paramChangers.forEach(c -> c.accept(metaObject));
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
