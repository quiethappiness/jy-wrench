package io.github.quiethappiness.wrench.util.web.config.domain.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;



/**
 * AbstractHandlerInterceptor
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象请求拦截器
 * @date 2025/8/18 16:30
 */
@Slf4j
public abstract class AbstractHandlerInterceptor implements HandlerInterceptor
{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		log.info("请求开始: {} {}", request.getMethod(), request.getRequestURI());
		boolean preHandle = HandlerInterceptor.super.preHandle(request, response, handler);
		if (request.getMethod()
			.equals("OPTIONS"))
		{
			// response.setStatus(200);
			return true;
		}
		return false;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
		log.info("请求结束: {} {}", request.getMethod(), request.getRequestURI());
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
	{
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}