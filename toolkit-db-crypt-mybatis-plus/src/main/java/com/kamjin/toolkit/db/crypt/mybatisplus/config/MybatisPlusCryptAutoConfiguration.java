package com.kamjin.toolkit.db.crypt.mybatisplus.config;

import com.kamjin.toolkit.db.crypt.core.bean.DbcryptProperties;
import com.kamjin.toolkit.db.crypt.core.executor.DefaultCryptExecutor;
import com.kamjin.toolkit.db.crypt.core.handler.DefaultAESCodecFieldValueHandler;
import com.kamjin.toolkit.db.crypt.mybatisplus.interceptor.MybatisPlusCryptInterceptor;
import com.kamjin.toolkit.db.crypt.mybatisplus.listener.MybatisplusConfigResolveByAppRefreshedListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kam
 * @since 2021/10/14
 *
 * <p>
 * mybatisplus加密自动配置
 * </p>
 */
@Configuration
@ConditionalOnClass(MybatisPlusCryptInterceptor.class)
@EnableConfigurationProperties(DbcryptProperties.class)
public class MybatisPlusCryptAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dbcrypt")
    public DbcryptProperties dbcryptProperties() {
        return new DbcryptProperties();
    }

    @Bean
    public MybatisplusConfigResolveByAppRefreshedListener mybatisplusConfigResolveByAppRefreshedListener(MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor) {
        return new MybatisplusConfigResolveByAppRefreshedListener(mybatisPlusCryptInterceptor);
    }

    @Bean
    public MybatisPlusCryptInterceptor mybatisPlusCryptInterceptor(DbcryptProperties dbcryptProperties) {
        return new MybatisPlusCryptInterceptor(dbcryptProperties, new DefaultCryptExecutor(new DefaultAESCodecFieldValueHandler(dbcryptProperties)));
    }
}
