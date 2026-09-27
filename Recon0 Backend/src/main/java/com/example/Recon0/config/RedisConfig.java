package com.example.Recon0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default configuration: 15 minutes TTL, JSON value serialization, String key serialization
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        // Specific cache TTL configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("achievements", defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put("programs", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("program_details", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("leaderboard", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("platform_analytics", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("program_analytics", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("org_dashboard", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("org_my_programs", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("admin_users", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("profiles", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
