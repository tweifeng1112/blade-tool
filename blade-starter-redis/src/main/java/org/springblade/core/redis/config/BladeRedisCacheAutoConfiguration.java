/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springblade.core.redis.config;

import org.springblade.core.tool.config.RedisConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展redis-cache支持注解cacheName添加超时时间
 *
 * @author L.cm
 */
@AutoConfiguration(before = RedisConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
public class BladeRedisCacheAutoConfiguration {

	/**
	 * 序列化方式
	 */
	private final RedisSerializer<Object> redisSerializer;
	private final CacheProperties cacheProperties;
	private final CacheManagerCustomizers customizerInvoker;
	@Nullable
	private final RedisCacheConfiguration redisCacheConfiguration;

	BladeRedisCacheAutoConfiguration(RedisSerializer<Object> redisSerializer,
									 CacheProperties cacheProperties,
									 CacheManagerCustomizers customizerInvoker,
									 ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
		this.redisSerializer = redisSerializer;
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
		this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
	}

	@Primary
	@Bean("redisCacheManager")
	public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
		RedisCacheConfiguration cacheConfiguration = this.determineConfiguration();
		List<String> cacheNames = this.cacheProperties.getCacheNames();
		Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
		if (!cacheNames.isEmpty()) {
			Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
			cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
			initialCaches.putAll(cacheConfigMap);
		}
		boolean allowInFlightCacheCreation = true;
		boolean enableTransactions = false;
		RedisAutoCacheManager cacheManager = new RedisAutoCacheManager(redisCacheWriter, cacheConfiguration, initialCaches, allowInFlightCacheCreation);
		cacheManager.setTransactionAware(enableTransactions);
		return this.customizerInvoker.customize(cacheManager);
	}

	private RedisCacheConfiguration determineConfiguration() {
		if (this.redisCacheConfiguration != null) {
			return this.redisCacheConfiguration;
		} else {
			CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
			RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
			config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
			if (redisProperties.getTimeToLive() != null) {
				config = config.entryTtl(redisProperties.getTimeToLive());
			}

			if (redisProperties.getKeyPrefix() != null) {
				config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
			}

			if (!redisProperties.isCacheNullValues()) {
				config = config.disableCachingNullValues();
			}

			if (!redisProperties.isUseKeyPrefix()) {
				config = config.disableKeyPrefix();
			}

			return config;
		}
	}
}
