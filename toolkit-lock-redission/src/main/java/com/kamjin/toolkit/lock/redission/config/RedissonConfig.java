package com.kamjin.toolkit.lock.redission.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Order(1)
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String urls;
    @Value("${spring.redis.password}")
    private String password;

    @Bean(name = "redissonClient")
    public RedissonClient redissonClientCluster() throws IOException {
        String[] nodes = urls.split(",");
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(nodes).setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}