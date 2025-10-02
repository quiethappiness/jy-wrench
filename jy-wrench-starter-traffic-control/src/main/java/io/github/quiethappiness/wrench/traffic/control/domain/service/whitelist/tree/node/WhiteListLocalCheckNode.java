package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhiteListLocalCheckNode extends AbstractWhiteListSupport
{
	private final WhiteListRedisCheckNode whiteListRedisCheckNode;
	final AntPathMatcher matcher = new AntPathMatcher();
	
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final WhiteListProperties whiteListProperties = requestParameter.getWhiteListProperties();
		final AttrValueResult attrValue = dynamicContext.getAttrValueResult();
		final String uri = dynamicContext.getUri();
		log.warn("开始检查本地白名单 id: {}", attrValue.userId);
		log.warn(Arrays.toString(whiteListProperties.getRules()));
		final boolean[] isInWhitelist = {false};
		final String finalUserId = attrValue.userId;
		Arrays.stream(whiteListProperties.getRules())
			.filter(localRule ->
			{
				// 添加null检查
				if (localRule == null)
				{
					return false;
				}
				boolean match = matcher.match(localRule.getUri(), uri);
				return match && localRule.getWhiteList() != null && localRule.getWhiteList()
					.containsKey(attrValue.type);
			}) // 匹配uri,这里的匹配方式是前缀匹配
			.findFirst()
			.ifPresent(localRule ->
			{
				// 添加null检查
				isInWhitelist[0] = localRule.getWhiteList()
					.get(attrValue.type)
					.contains(finalUserId);
			});
		if (isInWhitelist[0])
		{
			log.warn("用户{}在yml白名单中", attrValue.userId);
			return WhiteListResultEntity.builder()
				.inWhitListResult(new InWhitListResult(attrValue.userId, true))
				.build();
		}
		// todo:设置 是否在白名单中 isInWhitelist
		dynamicContext.setIsInWhitelist(isInWhitelist);
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListRedisCheckNode;
	}
}
