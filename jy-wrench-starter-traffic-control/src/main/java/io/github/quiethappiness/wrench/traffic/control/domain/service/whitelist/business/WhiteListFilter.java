package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Deprecated
// @Component
@Order(1)
@RequiredArgsConstructor
public class WhiteListFilter extends OncePerRequestFilter
{
	private final WhiteListService whitelistService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
	{
		// 这里会同时检测yml/properties文件和redis中的配置
		// 获取IP地址
		String ip = request.getRemoteAddr();
		String requestURI = request.getRequestURI();
		// 检查IP是否在白名单中
		if (!whitelistService.checkWhitelistId(requestURI, WhiteListType.IP, ip))
		{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		// 继续执行过滤器链
		filterChain.doFilter(request, response);
	}
}

