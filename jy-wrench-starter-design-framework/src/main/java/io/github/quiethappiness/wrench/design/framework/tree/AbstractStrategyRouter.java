package io.github.quiethappiness.wrench.design.framework.tree;

import lombok.Getter;
import lombok.Setter;

/**
 * @author quiethappiness @jingyue
 * @description 策略路由抽象类
 * @create 2024-12-14 13:25
 */

public abstract class AbstractStrategyRouter<T, D, R> implements StrategyMapper<T, D, R>, StrategyHandler<T, D, R>
{
	@Getter
	@Setter
	protected StrategyHandler<T, D, R> defaultStrategyHandler = DEFAULT;
	
	public R router(T requestParameter, D dynamicContext) throws Throwable
	{
		StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        if (null != strategyHandler)
        {
            return strategyHandler.apply(requestParameter, dynamicContext);
        }
		return defaultStrategyHandler.apply(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<T, D, R> get(T requestParameter, D dynamicContext) throws Exception
	{
		return null;
	}
}