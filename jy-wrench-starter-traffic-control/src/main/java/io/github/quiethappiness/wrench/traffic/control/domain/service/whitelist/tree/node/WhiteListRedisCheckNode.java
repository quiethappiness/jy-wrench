package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business.WhiteListService;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhiteListRedisCheckNode extends AbstractWhiteListSupport
{
	
	private final WhiteListService whitelistService;
	
	private final WhiteListEndNode whiteListEndNode;
	
	/**
	 * 执行基于Redis的白名单检查逻辑
	 * 
	 * @param requestParameter 请求参数实体，包含白名单相关配置信息
	 * @param dynamicContext 动态上下文，包含URI、属性值及白名单状态等运行时信息
	 * @return WhiteListResultEntity 白名单检查结果实体
	 * @throws Throwable 执行过程中可能抛出的异常
	 */
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final AttrValueResult attrValue = dynamicContext.getAttrValueResult();
		final String uri = dynamicContext.getUri();
		final boolean[] isInWhitelist = dynamicContext.getIsInWhitelist();
		try
		{
			// 执行数据库白名单检查
			log.warn("开始检查数据库白名单 id: {}", attrValue.actualValue());
			isInWhitelist[0] = whitelistService.checkWhitelistId(uri, attrValue.type(), attrValue.actualValue());
		}
		catch (Exception e)
		{
			// 记录白名单检查过程中的错误
			log.error("Error checking whitelist for user: {}", attrValue.actualValue(), e);
		}
		// 继续执行后续处理逻辑
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> get(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListEndNode;
	}
}