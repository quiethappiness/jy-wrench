package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhiteListBranchNode extends AbstractWhiteListSupport
{
	private final WhiteListDataNode whiteListDataNode;
	
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
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
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> get(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListDataNode;
	}
	
	/**
	 * 获取客户端真实IP地址
	 * @param request
	 * 	HttpServletRequest对象，包含客户端请求信息
	 * @return 客户端的真实IP地址字符串
	 */
	private static String getClientIp(HttpServletRequest request)
	{
		// 首先尝试从X-Forwarded-For头部获取IP地址
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			int index = ip.indexOf(',');
			if (index != -1)
			{
				// 如果包含多个IP地址，只返回第一个
				return ip.substring(0, index);
			}
			else
			{
				return ip;
			}
		}
		// 尝试从X-Real-IP头部获取IP地址
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		// 尝试从Proxy-Client-IP头部获取IP地址
		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		// 尝试从WL-Proxy-Client-IP头部获取IP地址
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		// 如果以上头部都不存在，则使用远程客户端的IP地址
		return request.getRemoteAddr();
	}
}