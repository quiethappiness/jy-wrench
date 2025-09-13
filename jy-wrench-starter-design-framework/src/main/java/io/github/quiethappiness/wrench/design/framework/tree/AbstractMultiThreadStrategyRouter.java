package io.github.quiethappiness.wrench.design.framework.tree;

/**
 * @author quiethappiness @jingyue
 * @description 异步资源加载策略
 * @create 2024-12-21 08:48
 */
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> extends AbstractStrategyRouter<T, D, R>
{
	@Override
	public R apply(T requestParameter, D dynamicContext) throws Throwable
	{
		// 异步加载数据
		multiThread(requestParameter, dynamicContext);
		// 业务流程受理
		return doApply(requestParameter, dynamicContext);
	}
	
	/**
	 * 异步加载数据
	 */
	protected abstract void multiThread(T requestParameter, D dynamicContext) throws Exception;
	
	/**
	 * 业务流程受理
	 */
	protected abstract R doApply(T requestParameter, D dynamicContext) throws Throwable;
}