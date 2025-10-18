package io.github.quiethappiness.wrench.util.design_framework.link.model2.null_check;

import io.github.quiethappiness.wrench.util.design_framework.link.model2.null_check.chain.BusinessLinkedList;
import io.github.quiethappiness.wrench.util.design_framework.link.model2.null_check.handler.IBusinessLogicHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author quiethappiness @jingyue
 * @description 链路装配
 * @create 2025-01-18 10:02
 */
@Getter
@Slf4j
public class BusinessLinkArmory<T, D, R>
{
	
	private final BusinessLinkedList<T, D, R> logicLink;
	
	@SafeVarargs
	public BusinessLinkArmory(String linkName, @NonNull IBusinessLogicHandler<T, D, R>... logicHandlers)
	{
		if (logicHandlers.length == 0)
		{
			throw new RuntimeException("逻辑链路不能为空");
		}
		// log.info("logicHandlers:{}", logicHandlers);
		logicLink = new BusinessLinkedList<>(linkName);
		for (IBusinessLogicHandler<T, D, R> logicHandler : logicHandlers)
		{
			logicLink.add(logicHandler);
		}
		// logicLink.printLinkList();
		
	}
	
	public BusinessLinkArmory(String linkName, @NonNull List<? extends IBusinessLogicHandler<T, D, R>> logicHandlers)
	{
		if (CollectionUtils.isEmpty(logicHandlers))
		{
			throw new RuntimeException("逻辑链路不能为空");
		}
		// log.info("logicHandlers:{}", logicHandlers);
		logicLink = new BusinessLinkedList<>(linkName);
		for (IBusinessLogicHandler<T, D, R> logicHandler : logicHandlers)
		{
			logicLink.add(logicHandler);
		}
		// logicLink.printLinkList();
	}
}