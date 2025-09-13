package io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.chain;

import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.AbstractDynamicContext;
import io.github.quiethappiness.wrench.design.framework.link.model2.LinkedList;
import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.handler.IBranchLogicHandler;

/**
 * @author quiethappiness @jignyue
 * @description 业务链路
 * @create 2025-01-18 10:27
 */
public class BranchLinkedList<T, D extends AbstractDynamicContext, R> extends LinkedList<IBranchLogicHandler<T, D, R>> implements IBranchLogicHandler<T, D, R>
{
	
	public BranchLinkedList(String name)
	{
		super(name);
	}
	
	@Override
	public R apply(T requestParameter, D dynamicContext) throws Exception
	{
		Node<IBranchLogicHandler<T, D, R>> current = this.getFirst();
		do
		{
			IBranchLogicHandler<T, D, R> item = current.getItem();
			R apply = item.apply(requestParameter, dynamicContext);
			// 是否是过程节点
			if (!dynamicContext.isProceed())
			{
				// 不是过程节点，直接返回结果
				return apply;
			}
			current = current.getNext();
		} while (null != current);
		return null;
	}
}