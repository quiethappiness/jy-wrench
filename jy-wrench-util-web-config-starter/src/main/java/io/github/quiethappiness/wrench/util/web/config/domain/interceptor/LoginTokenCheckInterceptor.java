package io.github.quiethappiness.wrench.util.web.config.domain.interceptor;

import io.github.quiethappiness.wrench.util.types.common.enums.AppExceptionType;
import io.github.quiethappiness.wrench.util.types.common.exception.AppException;
import io.github.quiethappiness.wrench.util.types.common.jjwt.WrenchJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * LoginTokenCheckInterceptor
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 针对前端请求是否携带loginToken进行拦截
 * @date 2025/8/15 16:02
 */
@Slf4j
public class LoginTokenCheckInterceptor extends AbstractHandlerInterceptor
{
	private final WrenchJwtUtil wrenchJwtUtil;
	
	private final long EDGE_TIME = TimeUnit.DAYS.toMillis(1); // 1天（毫秒）
	private final long OPENID_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(2); // 2小时（毫秒）
	
	// @Value("${app.config.domain}")
	private final String DOMAIN;
	
	public LoginTokenCheckInterceptor(WrenchJwtUtil wrenchJwtUtil, String DOMAIN)
	{
		this.wrenchJwtUtil = wrenchJwtUtil;
		this.DOMAIN = DOMAIN;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		// 直接允许options请求
		boolean preHandle = super.preHandle(request, response, handler);
		if (preHandle)
		{
			return true;
		}
		// 记录请求开始信息，包括请求方法和URI
		// 获取请求中的所有Cookie
		Cookie[] cookies = request.getCookies();
		if (null == cookies)
		{
			log.warn("未携带Cookie, 禁止访问");
			throw new AppException(AppExceptionType.NO_COOKIE);
		}
		// 使用Stream API查找名为"token"的Cookie
		Optional<Cookie> first = Arrays.stream(cookies)
			.filter(cookie -> "token".equals(cookie.getName()))
			.findFirst();
		// 检查是否找到token cookie且其值不为空
		if (!first.isPresent() || StringUtils.isBlank(first.get()
			.getValue()))
		{
			// 如果没有找到或值为空，则记录警告日志并拒绝访问
			log.warn("未携带token, 禁止访问");
			throw new AppException(AppExceptionType.NO_LOGIN_TOKEN);
		}
		// 获取登录令牌的值
		String token = first.get()
			.getValue();
		log.info("token: {}", token);
		// 验证JWT令牌是否过期，如果未过期则允许继续执行
		boolean tokenExpired = wrenchJwtUtil.isTokenExpired(token);
		if (tokenExpired)
		{
			// 如果令牌已过期，则记录警告日志并拒绝访问
			log.warn("token 已过期, 禁止访问");
			throw new AppException(AppExceptionType.LOGIN_TOKEN_EXPIRED);
		}
		// 如果令牌未过期，则允许继续执行
		Claims claims = wrenchJwtUtil.parseToken(token);
		if (claims == null)
		{
			log.warn("token 无效, 禁止访问");
			throw new AppException(AppExceptionType.INVALID_LOGIN_TOKEN);
		}
		String openid = claims.get("openid", String.class);
		if (StringUtils.isBlank(openid))
		{
			log.warn("token 无效, 禁止访问");
			throw new AppException(AppExceptionType.INVALID_LOGIN_TOKEN);
		}
		log.info("token有效, 允许访问");
		LoginUserContext.setOpenid(openid);
		LoginUserContext.setLoginToken(token);
		return true; // 返回true表示放行此请求
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
		try
		{
			if (request.getMethod()
				.equals("OPTIONS"))
			{
				// response.setStatus(200);
				return;
			}
			// 记录请求结束信息，包括请求方法和URI
			// 从用户上下文中获取当前登录的Token
			String token = LoginUserContext.getLoginToken();
			log.info("已经获得原始Token：{}", token);
			String openid = LoginUserContext.getOpenid();
			// 使用JWT工具解析Token，获取其中的声明信息
			Claims claims = wrenchJwtUtil.parseToken(token);
			log.info("已经获得原始Token，声明信息：{}", claims);
			// 获取Token的过期时间
			Date expiration = claims.getExpiration();
			// 检查Token是否即将过期（有效期小于5分钟）
			if (expiration.before(new Date(System.currentTimeMillis() + EDGE_TIME)))
			{
				// 当Token有效期小于5分钟时，需要刷新Token以延长会话时间
				// 重新生成新的Token，使用原有的用户主体信息
				// jwt这里限制时间
				Map<String, Object> stringObjectHashMap = new HashMap<>();
				stringObjectHashMap.put("openid", openid);
				stringObjectHashMap.put("ticket", claims.get("ticket"));
				String openidToken = wrenchJwtUtil.generateLoginToken(stringObjectHashMap);
				// 并没有保存到redis
				// 创建Cookie对象用于存储新的登录Token
				ResponseCookie cookie = ResponseCookie.from("token", openidToken)
					.domain(DOMAIN)
					// .domain("localhost")
					.path("/")
					.maxAge(6 * 24 * 60 * 60)
					// .httpOnly(true)  // ← 注释掉或删除这一行
					// .secure(true)      // 如果是 HTTPS
					.secure(false)
					.sameSite("None")
					.build();
				// 将Cookie添加到HTTP响应中，发送给客户端
				response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
			}
			// 返回true表示继续执行后续的请求处理链
			log.info("Token refresh completed.");
		}
		catch (Exception e)
		{
			log.error("Token refresh failed.", e);
			throw e;
		}
		finally
		{
			LoginUserContext.clear();
		}
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
	{
		super.afterCompletion(request, response, handler, ex);
		log.info("请求结束: {} {}", request.getMethod(), request.getRequestURI());
	}
	
	/**
	 * LoginUserContext
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description threadlocal
	 * @date 2025/8/15 21:36
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class LoginUserContext
	{
		// ThreadLocal 存储当前线程的用户 ID
		private static final ThreadLocal<String> OPENID = new ThreadLocal<>();
		private static final ThreadLocal<String> LOGIN_TOKEN = new ThreadLocal<>();
		
		/**
		 * 设置当前用户 ID
		 */
		public static void setOpenid(String userInfo)
		{
			OPENID.set(userInfo);
		}
		
		/**
		 * 获取当前用户 ID
		 */
		public static String getOpenid()
		{
			return OPENID.get();
		}
		
		/**
		 * 设置LoginToken
		 */
		public static void setLoginToken(String userInfo)
		{
			LOGIN_TOKEN.set(userInfo);
		}
		
		/**
		 * 获取LoginToken
		 */
		public static String getLoginToken()
		{
			return LOGIN_TOKEN.get();
		}
		
		/**
		 * 清除当前线程的数据，防止内存泄漏！
		 */
		public static void clearOpenid()
		{
			OPENID.remove();
		}
		
		public static void clearLoginToken()
		{
			LOGIN_TOKEN.remove();
		}
		
		public static void clear()
		{
			OPENID.remove();
			LOGIN_TOKEN.remove();
		}
	}
}