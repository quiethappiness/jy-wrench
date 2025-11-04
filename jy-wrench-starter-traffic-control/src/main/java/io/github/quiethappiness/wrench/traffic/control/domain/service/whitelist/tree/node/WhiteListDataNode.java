package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.FieldPart;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory.DynamicContext;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList.WhiteListType;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhiteListDataNode extends AbstractWhiteListSupport
{
	private final WhiteListLocalCheckNode whiteListLocalCheckNode;
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, DynamicContext dynamicContext) throws Throwable
	{
		TcWhiteList tcWhiteList = requestParameter.tcWhiteList();
		ProceedingJoinPoint jp = requestParameter.jp();
		final AttrValueResult attrValue = getAttrValue(jp, tcWhiteList);
		// todo:设置 白名单校验对象 attrValueResult
		dynamicContext.setAttrValueResult(attrValue);
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, DynamicContext dynamicContext) throws Exception
	{
		return whiteListLocalCheckNode;
	}
	
	/**
	 * 从方法参数中提取白名单属性值
	 * 
	 * @param jp 连接点对象，包含方法执行的相关信息
	 * @param tcWhiteList 白名单注解对象，包含白名单配置信息
	 * @return AttrValueResult 包含用户ID和白名单类型的属性值结果对象
	 */
	private static AttrValueResult getAttrValue(ProceedingJoinPoint jp, TcWhiteList tcWhiteList)
	{
		// 获取白名单键值，优先使用key()，如果为空则使用whiteListField()
		String key = StringUtils.hasText(tcWhiteList.key()) ? tcWhiteList.key() : tcWhiteList.whiteListField();
		WhiteListType type = tcWhiteList.type();
		String userId = FieldPart.extractSpelExpressionValue(jp, key,String.class);
		// Check if actualValue is null or empty
		if (userId == null || userId.isEmpty())
		{
			log.warn("User ID is null or empty, setting to 'unknown'");
			userId = "unknown";
		}
		// 返回包含白名单类型和用户ID的结果对象
		return new AttrValueResult(type, userId);
	}
	
	
}