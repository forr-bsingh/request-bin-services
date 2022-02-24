package com.github.requestbin;

import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Arrays;
import java.util.Collection;

@RefreshScope
@Configuration
public class ApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    @Value("${redis.cluster.nodes}")
    private String[] clusterNodes;

    @Value("${redis.standalone.host}")
    private String standaloneHost;

    @Value("${redis.standalone.port}")
    private int standalonePort;

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "jedisConnectionFactory")
    @Primary
    @ConditionalOnProperty(value = "redis.mode", havingValue = "standalone")
    public JedisConnectionFactory standaloneConnectionFactory() {
        LOGGER.info("Setting up for standalone mode.");
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(standaloneHost, standalonePort));
    }

    @Bean(name = "jedisConnectionFactory")
    @Primary
    @ConditionalOnProperty(value = "redis.mode", havingValue = "cluster")
    public JedisConnectionFactory clusterConnectionFactory() {
        LOGGER.info("Setting up for cluster mode.");
        return new JedisConnectionFactory(new RedisClusterConfiguration(Arrays.asList(clusterNodes)));
    }

    @GrpcGlobalServerInterceptor
    public ServerInterceptor grpcLogInterceptor() {
        return new GRPCLogInterceptor();
    }
}
