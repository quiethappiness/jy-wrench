package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist;

import io.github.quiethappiness.wrench.traffic.control.config.TrafficControlProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IWhiteListAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business.WhiteListService;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.WhiteListChecker;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
public abstract class AbstractWhiteListAop implements IWhiteListAOP
{
	@Resource
	protected WhiteListService whitelistService;
	
	@Resource
	protected TrafficControlProperties trafficControlProperties;
	final AntPathMatcher matcher = new AntPathMatcher();
	
	protected Result doCheck(ProceedingJoinPoint jp, ServletRequestAttributes attributes, WhiteListChecker whiteListChecker)
	{
		HttpServletRequest request = attributes.getRequest();
		// 提取请求信息
		String ip = getClientIp(request);
		String method = request.getMethod();
		// StringBuffer requestURL = request.getRequestURL();
		final String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		String userAgent = request.getHeader("User-Agent");
		log.info("请求开始 - IP: {}, Method: {}, URI: {}, QueryString: {}, UserAgent: {}",
			ip, method, uri, queryString, userAgent);
		String key = whiteListChecker.key();
		WhiteListType type = whiteListChecker.type();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String[] paramNames = signature.getParameterNames();
		Object[] args = jp.getArgs();
		EvaluationContext context = new StandardEvaluationContext();
		if (paramNames != null)
		{
			for (int i = 0; i < paramNames.length; i++)
			{
				context.setVariable(paramNames[i], args[i]);
			}
		}
		ExpressionParser parser = new SpelExpressionParser();
		String userId = parser.parseExpression(key)
			.getValue(context, String.class);
		// 先根据yml配置进行校验
		log.warn(Arrays.toString(trafficControlProperties.getRules()));
		final boolean[] isInWhitelist = {false};
		Arrays.stream(trafficControlProperties.getRules())
			.filter(localRule ->
			{
				boolean match = matcher.match(localRule.getUri(), uri);
				return match && localRule.getWhiteList() != null && localRule.getWhiteList()
					.containsKey(type);
			}) // 匹配uri,这里的匹配方式是前缀匹配
			.findFirst()
			.ifPresent(localRule ->
			{
				isInWhitelist[0] = localRule.getWhiteList()
					.get(type)
					.contains(userId);
			});
		if (isInWhitelist[0])
		{
			log.warn("用户{}在yml白名单中", userId);
			return new Result(userId, true);
		}
		// 检查白名单
		isInWhitelist[0] = whitelistService.checkWhitelistId(type, userId);
		return new Result(userId, isInWhitelist[0]);
	}
	
	/**
	 * 获取客户端真实IP地址
	 */
	protected String getClientIp(HttpServletRequest request)
	{
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip))
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
		if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	protected static class Result
	{
		public final String userId;
		public final boolean isInWhitelist;
		
		public Result(String userId, boolean isInWhitelist)
		{
			this.userId = userId;
			this.isInWhitelist = isInWhitelist;
		}
	}
}
