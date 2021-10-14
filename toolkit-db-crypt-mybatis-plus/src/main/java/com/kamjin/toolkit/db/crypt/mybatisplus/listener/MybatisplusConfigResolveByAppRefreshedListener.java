package com.kamjin.toolkit.db.crypt.mybatisplus.listener;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.MybatisPlusCryptInterceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author kam
 * @since 2021/10/14
 *
 * <p>
 * 应用监听配置解析处理
 * </p>
 */
public class MybatisplusConfigResolveByAppRefreshedListener implements ApplicationListener<ApplicationEvent> {

    private MybatisplusConfigResolveByAppRefreshedListener() {
    }

    public MybatisplusConfigResolveByAppRefreshedListener(MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor) {
        this.mybatisPlusCryptInterceptor = mybatisPlusCryptInterceptor;
    }

    private MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            ContextRefreshedEvent event = (ContextRefreshedEvent) applicationEvent;
            SqlSessionFactory sessionFactory = event.getApplicationContext().getBean(SqlSessionFactory.class);
            Configuration configuration = sessionFactory.getConfiguration();
            if (configuration instanceof MybatisConfiguration) {
                MybatisConfiguration mybatisConfiguration = (MybatisConfiguration) configuration;
                boolean paginationInterceptorRegistered = mybatisConfiguration.getInterceptors().stream().anyMatch(x -> x instanceof PaginationInterceptor);
                //如果已注册了paginationInterceptor插件 则mp分页查询时，会生成countSql countSql会将加密后的参数传入到真实page查询中，所以当statement为page类型查询时 需要跳过加密操作

                //此处更改是否开启paginationInterceptor的标识
                mybatisPlusCryptInterceptor.setRegisteredPaginationInterceptor(paginationInterceptorRegistered);
            }
        }
    }
}
