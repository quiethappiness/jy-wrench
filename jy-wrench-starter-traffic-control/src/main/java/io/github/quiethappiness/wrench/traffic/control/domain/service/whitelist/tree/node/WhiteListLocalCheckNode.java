package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
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
	private final AntPathMatcher matcher = new AntPathMatcher();
	
	/**
	 * 执行本地白名单检查逻辑
	 * 
	 * @param requestParameter 请求参数实体，包含白名单配置等信息
	 * @param dynamicContext 动态上下文，包含URI、属性值等运行时信息
	 * @return WhiteListResultEntity 白名单检查结果实体
	 * @throws Throwable 执行过程中可能抛出的异常
	 */
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final WhiteListProperties whiteListProperties = requestParameter.whiteListProperties();
		final String uri = dynamicContext.getUri();
		final AttrValueResult attrValueResult = dynamicContext.getAttrValueResult();
		final String actualValue = attrValueResult.actualValue();
		final TcWhiteList.WhiteListType type = attrValueResult.type();
		log.warn("开始检查本地白名单 id: {}", actualValue);
		log.warn(Arrays.toString(whiteListProperties.getRules()));
		final boolean[] isInWhitelist = {false};
		// 遍历白名单规则并过滤匹配的规则
		Arrays.stream(whiteListProperties.getRules())
			.filter(localRule ->
			{
				// 添加null检查
				if (localRule == null)
				{
					return false;
				}
				// 检查URI是否匹配且白名单类型存在
				boolean match = matcher.match(localRule.getUri(), uri);
				return match && localRule.getWhiteList() != null && localRule.getWhiteList()
					.containsKey(type);
			}) // 匹配uri,这里的匹配方式是前缀匹配
			.findFirst()
			.ifPresent(localRule ->
			{
				// 添加null检查
				isInWhitelist[0] = localRule.getWhiteList()
					.get(type)
					.contains(actualValue);
			});
		// 如果在白名单中，直接返回结果
		if (isInWhitelist[0])
		{
			log.warn("用户{}在yml白名单中", attrValueResult.actualValue());
			return WhiteListVO.WhiteListResultEntity
				.builder()
				.inWhitListResult(new InWhitListResult(attrValueResult.actualValue(), true))
				.build();
		}
		// todo:设置 是否在白名单中 isInWhitelist
		dynamicContext.setIsInWhitelist(isInWhitelist);
		// 继续执行后续处理逻辑
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> get(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListRedisCheckNode;
	}
}