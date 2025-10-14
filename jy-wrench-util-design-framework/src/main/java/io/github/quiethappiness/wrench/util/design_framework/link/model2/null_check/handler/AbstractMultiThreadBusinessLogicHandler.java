package io.github.quiethappiness.wrench.util.design_framework.link.model2.null_check.handler;

public abstract class AbstractMultiThreadBusinessLogicHandler<T,D,R> implements IBusinessLogicHandler<T,D,R>
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
	 * 执行限流申请处理
	 *
	 * @param requestParameter 限流参数实体，包含限流开关等配置信息
	 * @param dynamicContext 动态上下文，用于在限流策略执行过程中传递状态信息
	 * @return RateLimiterReturnResultEntity 限流处理结果实体
	 * @throws Throwable 处理过程中可能抛出的异常
	 */
	protected abstract R doApply(T requestParameter, D dynamicContext) throws Throwable;
}
