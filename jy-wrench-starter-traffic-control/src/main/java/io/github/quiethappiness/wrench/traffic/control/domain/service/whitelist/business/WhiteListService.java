package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.lua.manager.domain.service.redis.impl.IRedisService;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

// 你的组件核心服务
// @ConditionalOnBean(WhiteListDataProvider.class)
@Component
@RequiredArgsConstructor
@Slf4j
public class WhiteListService extends AbstractWhiteListService
{
	private final IRedisService redisService;
	private final List<WhiteListDataProvider> dataProviders;
	// private final ILuaScriptManager scriptManager;
	// 通过构造器注入依赖
	
	@PostConstruct
	private void initWhitelist()
	{
		dataProviders.parallelStream()
			.forEach((dataProvider) ->
				{
					log.info("Initializing whitelist for {}", dataProvider.getType());
					dataProvider.getWhitelistData()
						.forEach(data ->
						{
							String spliceHashMapName = spliceHashMapName(data.getUri());
							log.info("Creating local cached map for {}", spliceHashMapName);
							// 使用新的包路径创建配置选项
							LocalCachedMapOptions<WhiteListType, List<String>> options = LocalCachedMapOptions.name(spliceHashMapName);
							setLocalCacheMapOptions(options);
							RLocalCachedMap<WhiteListType, List<String>> map = redisService.getLocalCachedMap(options);
							map.putAll(data.getWhiteList());
						});
				}
			);
	}
	
	public boolean checkWhitelistId(String uri, WhiteListType type, String userId)
	{
		// 这个只是全匹配，没法模糊匹配
		RKeys key = redisService.getKey();
		key.
		String spliceHashMapName = spliceHashMapName(uri);
		LocalCachedMapOptions<WhiteListType, List<String>> options = LocalCachedMapOptions.name(spliceHashMapName);
		RLocalCachedMap<WhiteListType, List<String>> whiteList = redisService.getLocalCachedMap(options);
		log.info("Checking whitelist for {}", userId);
		List<String> strings = whiteList.get(type);
		return strings != null && strings.contains(userId);
	}
}
