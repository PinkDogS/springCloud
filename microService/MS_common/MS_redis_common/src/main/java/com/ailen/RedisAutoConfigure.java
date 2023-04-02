package com.ailen;

import com.ailen.aop.LockAop;
import com.ailen.template.RedisRepository;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis 配置类
 *
 * @author anshare
 *
 **/
@EnableConfigurationProperties({RedisProperties.class})
/**
 * 来缓存方法的结果,在启用缓存后，可以使用Spring框架提供的缓存注解@Cacheable、@CachePut和@CacheEvict等来标注需要缓存的方法
 * Cacheable 先查看缓存中有无，没有执行方法，将缓存结果存入缓存
 * CachePut 先执行方法，然后将缓存存入
 * 会创建缓存管理器，暂存方法结果
 */
@EnableCaching
public class RedisAutoConfigure {

    /**
     * RedisTemplate配置
     *
     * @param factory
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedissonConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        RedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * Redis repository redis repository.
     *
     * @param redisTemplate the redis template
     * @return the redis repository
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRepository(redisTemplate);
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":" + method.getName() + ":");
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    @ConditionalOnMissingBean(LockAop.class)
    public LockAop lockAop() {
        return new LockAop();
    }
}
