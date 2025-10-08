package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node.WhiteListRootNode;
import io.github.quiethappiness.wrench.util.design_framework.tree.AbstractStrategyFactory;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class WhiteListStrategyFactory extends AbstractStrategyFactory<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity>
{
	private final WhiteListRootNode whiteListRootNode;
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, DynamicContext, WhiteListResultEntity> strategyHandler() throws Exception
	{
		return whiteListRootNode;
	}
	
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class DynamicContext
	{
		private ServletRequestAttributes servletRequestAttributes;
		private String uri;
		private AbstractWhiteListSupport.AttrValueResult attrValueResult;
		private boolean[] isInWhitelist;
	}
}
