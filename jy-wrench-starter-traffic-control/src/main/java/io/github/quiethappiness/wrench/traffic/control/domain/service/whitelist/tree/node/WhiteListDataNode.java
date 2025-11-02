package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhiteListDataNode extends AbstractWhiteListSupport
{
	private final WhiteListLocalCheckNode whiteListLocalCheckNode;
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		TcWhiteList tcWhiteList = requestParameter.tcWhiteList();
		ProceedingJoinPoint jp = requestParameter.jp();
		final AttrValueResult attrValue = getAttrValue(jp, tcWhiteList);
		// todo:设置 白名单校验对象 attrValueResult
		dynamicContext.setAttrValueResult(attrValue);
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> get(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
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
		TcWhiteList.WhiteListType type = tcWhiteList.type();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String[] paramNames = signature.getParameterNames();
		Object[] args = jp.getArgs();
		// 创建表达式计算上下文，并将方法参数设置为变量
		EvaluationContext context = new StandardEvaluationContext();
		if (paramNames != null)
		{
			for (int i = 0; i < paramNames.length; i++)
			{
				context.setVariable(paramNames[i], args[i]);
			}
		}
		ExpressionParser parser = new SpelExpressionParser();
		String userId;
		// Validate the expression key before parsing
		if (!StringUtils.hasText(key))
		{
			log.warn("Expression key is null or empty, using default value");
			userId = "unknown";
		}
		else
		{
			try
			{
				// 处理 #{variable} 格式的表达式，将其转换为 #variable 格式
				if (key.startsWith("#{") && key.endsWith("}"))
				{
					key = "#" + key.substring(2, key.length() - 1);
				}
				// 解析表达式获取用户ID
				userId = parser.parseExpression(key)
					.getValue(context, String.class);
			}
			catch (Exception e)
			{
				log.error("Failed to parse expression: {}, using default value", key, e);
				userId = "unknown";
			}
		}
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