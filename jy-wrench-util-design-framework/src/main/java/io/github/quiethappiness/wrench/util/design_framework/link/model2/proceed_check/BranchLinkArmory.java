package io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check;

import io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check.chain.BranchLinkedList;
import io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check.handler.IBranchLogicHandler;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * @author quiethappiness @jingyue
 * @description 链路装配
 * @create 2025-01-18 10:02
 */
@Getter
public class BranchLinkArmory<T, D extends AbstractDynamicContext, R>
{
	private final BranchLinkedList<T, D, R> logicLink;
	
	@SafeVarargs
	public BranchLinkArmory(String linkName, @NonNull IBranchLogicHandler<T, D, R>... logicHandlers)
	{
		logicLink = new BranchLinkedList<>(linkName);
		for (IBranchLogicHandler<T, D, R> logicHandler : logicHandlers)
		{
			logicLink.add(logicHandler);
		}
	}
	
	public BranchLinkArmory(String linkName, @NonNull List<? extends IBranchLogicHandler<T, D, R>> logicHandlers)
	{
		logicLink = new BranchLinkedList<>(linkName);
		for (IBranchLogicHandler<T, D, R> logicHandler : logicHandlers)
		{
			logicLink.add(logicHandler);
		}
	}
}