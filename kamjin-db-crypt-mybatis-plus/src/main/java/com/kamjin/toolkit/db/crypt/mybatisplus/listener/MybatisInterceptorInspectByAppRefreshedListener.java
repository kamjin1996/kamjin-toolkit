package com.kamjin.toolkit.db.crypt.mybatisplus.listener;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.ModifyParamValueInterceptor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author kam
 * @since 2021/10/14
 *
 * <p>
 * mybatis插件监察监听器
 * </p>
 */
@Order(-2)
public class MybatisInterceptorInspectByAppRefreshedListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger log = LoggerFactory.getLogger(ModifyParamValueInterceptor.class);

    public MybatisInterceptorInspectByAppRefreshedListener() {
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            ContextRefreshedEvent event = (ContextRefreshedEvent) applicationEvent;
            SqlSessionFactory sessionFactory = event.getApplicationContext().getBean(SqlSessionFactory.class);
            Configuration configuration = sessionFactory.getConfiguration();
            if (configuration instanceof MybatisConfiguration) {
                MybatisConfiguration mybatisConfiguration = (MybatisConfiguration) configuration;
                boolean paginationInterceptorRegistered = mybatisConfiguration.getInterceptors().stream().anyMatch(x -> x instanceof PaginationInterceptor);

                //如果是注册了paginationInterceptor的情况下，mp分页查询时，page对象的searchCount参数默认是true
                //分页插件会生成一条countSql查询数据库，如果有需要加密的参数作为条件，那么其携带的参数是未加密的参数，
                // 自然而然就查不到了，这并不是我们期望的结果，所以需要在paginationInterceptor执行前，强制变更page参数的searchCount参数为false
                if (paginationInterceptorRegistered) {
                    //再注册一个更改指定参数的拦截器 这个拦截器因为是后于pageInterceptor后面注册的，因为mybatis插件执行顺序是由后向前，所以会比pageInterceptor先执行

                    Consumer<MetaObject> pageSearchCount2False = metaObject -> {
                        IPage<?> page = obtainPageParam(metaObject);
                        if (page == null) {
                            return;
                        }
                        if (page instanceof Page) {
                            //强制更改searchCount为false
                            ((Page<?>) page).setSearchCount(false);
                        } else {
                            log.error("错误，当前未支持非Page对象的操作，请使用Page或其子类作为mybatisplus的分页查询返回对象");
                        }
                    };

                    ModifyParamValueInterceptor interceptor = new ModifyParamValueInterceptor();
                    interceptor.addParamChanger(pageSearchCount2False);

                    //注册
                    mybatisConfiguration.addInterceptor(interceptor);
                }
            }
        }
    }

    /**
     * 从MetaObject获取Page对象
     *
     * @param metaObject 元数据参数对象
     * @return 分页对象
     */
    private IPage<?> obtainPageParam(MetaObject metaObject) {
        //以下代码摘自：PaginationInterceptor 主要是拿到page对象
        //========================================================================

        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return null;
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();

        // 判断参数里是否有page对象
        IPage<?> page = null;
        if (paramObj instanceof IPage) {
            page = (IPage<?>) paramObj;
        } else if (paramObj instanceof Map) {
            for (Object arg : ((Map<?, ?>) paramObj).values()) {
                if (arg instanceof IPage) {
                    page = (IPage<?>) arg;
                    break;
                }
            }
        }

        //========================================================================
        return page;
    }
}
