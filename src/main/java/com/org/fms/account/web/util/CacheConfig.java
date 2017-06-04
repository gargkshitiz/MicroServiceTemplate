package com.org.fms.account.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Configures Redis cache
 * @author Kshitiz Garg
 */
@Configuration
@EnableCaching
@PropertySource("classpath:redis.properties")
public class CacheConfig extends CachingConfigurerSupport{
	
	protected Logger logger = LoggerFactory.getLogger(CacheConfig.class);
	
	@Value("${redis.host:localhost}")
	private String redisHost;
	@Value("${redis.port:6379}")
	private int redisPort;
	
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setExposeConnection(true);
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    
    @Bean
    public RedisCacheManager cacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        redisCacheManager.setTransactionAware(true);
        redisCacheManager.setLoadRemoteCachesOnStartup(true);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
    }

    @Override
	public CacheErrorHandler errorHandler(){
		return new CacheErrorHandler(){
			@Override
			public void handleCacheClearError(RuntimeException ex, Cache cache) {
				logger.warn("Exception occured when clearing cache: {}", cache.getName(), ex);
			}

			@Override
			public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
				logger.warn("Exception occured when eviciting an item from cache. Key : {} , Cache : {}", key, cache.getName(), ex);
			}

			@Override
			public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
				logger.warn("Exception occured when fetching an item from cache. Key : {} , Cache : {}", key, cache.getName(), ex);
			}

			@Override
			public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
				logger.warn("Exception occured when updating an item in cache. Key : {} , Cache : {}, Value: {}", key, cache.getName(), value, ex);
			}
		};
	}
}