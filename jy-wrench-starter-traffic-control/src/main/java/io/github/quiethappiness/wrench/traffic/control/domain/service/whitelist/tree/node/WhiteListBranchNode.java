package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhiteListBranchNode extends AbstractWhiteListSupport
{
	private final WhiteListDataNode whiteListDataNode;
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final ServletRequestAttributes attributes = dynamicContext.getServletRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 提取请求信息
		String ip = getClientIp(request);
		String method = request.getMethod();
		// StringBuffer requestURL = request.getRequestURL();
		final String uri = request.getRequestURI();
		// todo:设置uri
		dynamicContext.setUri(uri);
		String queryString = request.getQueryString();
		String userAgent = request.getHeader("User-Agent");
		log.info("请求开始 - IP: {}, Method: {}, URI: {}, QueryString: {}, UserAgent: {}",
			ip, method, uri, queryString, userAgent);
		return router(requestParameter,dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListDataNode;
	}
	
	/**
	 * 获取客户端真实IP地址
	 */
	private static String getClientIp(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			int index = ip.indexOf(',');
			if (index != -1)
			{
				return ip.substring(0, index);
			}
			else
			{
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		return request.getRemoteAddr();
	}
}
