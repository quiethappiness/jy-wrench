package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.wrench.lua.manager.domain.service.base.impl.IRedisService;
import io.github.quiethappiness.wrench.dynamic.config.center.config.DCCAutoProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.data.WhiteListDataProvider;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.List;

// 你的组件核心服务
@Component
@Slf4j
public class WhiteListService extends AbstractWhiteListService
{
	private final List<WhiteListDataProvider> dataProviders;
	// private final ILuaScriptManager scriptManager;
	// 通过构造器注入依赖
	
	public WhiteListService(
		List<WhiteListDataProvider> dataProviders,
		DCCAutoProperties DCCAutoProperties,
		IRedisService redisService
	)
	{
		log.info("Initializing whitelist service");
		this.dataProviders = dataProviders;
		super.DCCAutoProperties = DCCAutoProperties;
		super.redisService = redisService;
	}
	
	@PostConstruct
	@Override
	protected void initWhitelist()
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
	
	@Override
	public boolean checkWhitelistId(String uri, WhiteListType type, String userId)
	{
		// 这个只是全匹配，没法模糊匹配
		log.info("Checking whitelist for {}", userId);
		String spliceHashMapName = spliceHashMapName(uri);
		boolean result = doCheckFromRMap(type, spliceHashMapName, userId);
		if (result)
		{
			return true;
		}
		// 这里尝试模糊匹配
		String uriPrefix = uri.substring(0, uri.lastIndexOf("/"));
		String spliceHashMapNameWithPrefix = spliceHashMapName(uriPrefix + "/*");
		RKeys rKeys = redisService.getKey();
		Iterable<String> keysByPattern = rKeys.getKeysWithLimit(spliceHashMapNameWithPrefix, 100);
		final AntPathMatcher matcher = new AntPathMatcher();
		for (String key : keysByPattern)
		{
			boolean match = matcher.match(key, uri);
			if (!match)
			{
				continue;
			}
			if (doCheckFromRMap(type, key, userId))
			{
				return true;
			}
		}
		return false;
	}
}
