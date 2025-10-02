package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
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
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		TcWhiteList tcWhiteList = requestParameter.getTcWhiteList();
		ProceedingJoinPoint jp = requestParameter.getJp();
		final AttrValueResult attrValue = getAttrValue(jp, tcWhiteList);
		// todo:设置 白名单校验对象 attrValueResult
		dynamicContext.setAttrValueResult(attrValue);
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListLocalCheckNode;
	}
	
	private static AttrValueResult getAttrValue(ProceedingJoinPoint jp, TcWhiteList tcWhiteList)
	{
		String key = StringUtils.hasText(tcWhiteList.key()) ? tcWhiteList.key() : tcWhiteList.whiteListField();
		WhiteListType type = tcWhiteList.type();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String[] paramNames = signature.getParameterNames();
		Object[] args = jp.getArgs();
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
				userId = parser.parseExpression(key)
					.getValue(context, String.class);
			}
			catch (Exception e)
			{
				log.error("Failed to parse expression: {}, using default value", key, e);
				userId = "unknown";
			}
		}
		// Check if userId is null or empty
		if (userId == null || userId.isEmpty())
		{
			log.warn("User ID is null or empty, setting to 'unknown'");
			userId = "unknown";
		}
		return new AttrValueResult(type, userId);
	}
}

