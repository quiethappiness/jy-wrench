package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.lua.manager.domain.service.redis.IRedisService;
import io.github.quiethappiness.wrench.dynamic.config.center.config.DynamicConfigCenterAutoProperties;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.func.SlidingWindowRateLimiter.MAO_HAO;

// 你的组件核心服务
@ConditionalOnBean(WhiteListDataProvider.class)
@Component
@RequiredArgsConstructor
@Slf4j
public class WhiteListService
{
	private final IRedisService redisService;
	private final List<WhiteListDataProvider> dataProviders;
	private final DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties;
	// private final ILuaScriptManager scriptManager;
	// 通过构造器注入依赖
	
	@PostConstruct
	private void initWhitelist()
	{
		dataProviders.parallelStream()
			.forEach((dataProvider) ->
			{
				WhiteListType type = dataProvider.getType();
				RBitSet bitSet = redisService.getBitSet(spliceBitSetName(type.getDataProviderName()));
				log.info("Initializing whitelist for {}", type.getCode());
				dataProvider.getWhitelistData()
					.forEach((userId ->
						bitSet.set(redisService.getBitIndex(userId), true)));
			});
	}
	
	public boolean checkWhitelistId(WhiteListType type, String userId)
	{
		return redisService.getBitSet(spliceBitSetName(type.getDataProviderName()))
			.get(redisService.getBitIndex(userId));
	}
	
	private String spliceBitSetName(String name)
	{
		return dynamicConfigCenterAutoProperties.getSystem() + MAO_HAO + "whitelist" + MAO_HAO + getClass().getTypeName() + MAO_HAO + name;
	}
}
