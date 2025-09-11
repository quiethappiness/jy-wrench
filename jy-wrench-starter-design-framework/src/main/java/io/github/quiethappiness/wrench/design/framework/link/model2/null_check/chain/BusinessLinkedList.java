package io.github.quiethappiness.wrench.design.framework.link.model2.null_check.chain;

import io.github.quiethappiness.wrench.design.framework.link.model2.LinkedList;
import io.github.quiethappiness.wrench.design.framework.link.model2.null_check.handler.IBusinessLogicHandler;

/**
 * @author quiethappiness @jingyue
 * @description 业务链路
 * @create 2025-01-18 10:27
 */
public class BusinessLinkedList<T, D, R> extends LinkedList<IBusinessLogicHandler<T, D, R>> implements IBusinessLogicHandler<T, D, R>
{
	
	public BusinessLinkedList(String name)
	{
		super(name);
	}
	
	@Override
	public R apply(T requestParameter, D dynamicContext) throws Exception
	{
		Node<IBusinessLogicHandler<T, D, R>> current = this.getFirst();
		do
		{
			IBusinessLogicHandler<T, D, R> item = current.getItem();
			R apply = item.apply(requestParameter, dynamicContext);
			if (null != apply)
			{
				return apply;
			}
			current = current.getNext();
		} while (null != current);
		return null;
	}
}