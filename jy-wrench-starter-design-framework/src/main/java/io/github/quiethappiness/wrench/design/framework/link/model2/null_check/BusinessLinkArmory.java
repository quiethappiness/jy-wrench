package io.github.quiethappiness.wrench.design.framework.link.model2.null_check;

import io.github.quiethappiness.wrench.design.framework.link.model2.null_check.chain.BusinessLinkedList;
import io.github.quiethappiness.wrench.design.framework.link.model2.null_check.handler.IBusinessLogicHandler;
import lombok.Getter;
import org.springframework.lang.NonNull;

/**
 * @author quiethappiness @jingyue
 * @description 链路装配
 * @create 2025-01-18 10:02
 */
@Getter
public class BusinessLinkArmory<T, D, R>
{
	
	private final BusinessLinkedList<T, D, R> logicLink;
	
	@SafeVarargs
	public BusinessLinkArmory(String linkName, @NonNull IBusinessLogicHandler<T, D, R>... logicHandlers)
	{
		logicLink = new BusinessLinkedList<>(linkName);
		for (IBusinessLogicHandler<T, D, R> logicHandler : logicHandlers)
		{
			logicLink.add(logicHandler);
		}
	}
}