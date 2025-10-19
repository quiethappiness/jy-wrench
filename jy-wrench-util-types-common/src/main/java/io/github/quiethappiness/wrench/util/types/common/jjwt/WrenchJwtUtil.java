package io.github.quiethappiness.wrench.util.types.common.jjwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WrenchJwtUtil
 * @author quietHappiness
 * @version 1.0
 * @description jwt工具类
 * @date 2025/8/14 23:00
 */
@Slf4j
public class WrenchJwtUtil
{
	// subject 主题（通常是用户ID）
	private final String LOGIN_SUBJECT = "login";
	private final String OTHER_SUBJECT = "other";
	// 🔐 秘钥（至少 256-bit，即 32 字节）
	private final String SECRET_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqNF1fuyoSl7DNWEkhxr9Z4safkIXavkjnpYc5tR5pCZwp5avyf4pv7Un2CGnMaTA8Pdty14eku+8lKO/K8+2mNWl4CF8orzAv4JPoFROeB2iBfOZuEnF45o//gF9ScIJnNVLi6/FiCUEkuVrucSA9HMYwQwCM1ul3uzmq4iFIQetByl+5AVgFcc4SwJMK2CKtXEnf4rqxshKi11cdn2KiO4lzDI1DSX2WV9ZwEN6Hl8XSvRSkIy1IgJk8iwiOxg7WOn4SEqcSzdfaHqlGef8nSNhrI2IXlMOzI0egwhkRFE2LadtkUn1iOSmHPajWufqY5L2x65/11Veq1tGncgHlQIDAQAB"; // 必须至少 32 字符（256位）
	
	private final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(7); // 7days（毫秒）
	
	// 使用 HMAC-SHA256 算法的密钥
	private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	
	public String generateLoginToken(Map<String, Object> claims)
	{
		return generateToken(LOGIN_SUBJECT, claims, EXPIRATION_TIME);
	}
	
	/**
	 * 生成 JWT Token
	 * @param claims
	 * 	额外信息（如角色、权限等）
	 * @param openidExpirationTime
	 * @return token
	 */
	public String generateLoginToken(Map<String, Object> claims, long openidExpirationTime)
	{
		return generateToken(LOGIN_SUBJECT, claims, openidExpirationTime);
	}
	
	/**
	 * 生成 JWT Token
	 * @param subject
	 * 	主题（通常是用户ID）
	 * @param claims
	 * 	额外信息（如角色、权限等）
	 * @return token
	 */
	public String generateToken(String subject, Map<String, Object> claims)
	{
		return generateToken(subject, claims, EXPIRATION_TIME);
	}
	
	/**
	 * 生成 JWT Token
	 * @param subject
	 * 	用户标识（通常为用户ID或用户名）
	 * @param claims
	 * 	自定义声明信息，包含需要存储在token中的额外数据
	 * @param expiration
	 * 	过期时间（毫秒），表示token的有效时长
	 * @return 生成的JWT字符串token
	 */
	public String generateToken(String subject, Map<String, Object> claims, long expiration)
	{
		return Jwts.builder()
			.claims(claims)                                            // 设置自定义声明信息
			.subject(subject)                                          // 设置用户标识
			.issuedAt(new Date())                                      // 设置签发时间（当前时间）
			.expiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
			.signWith(key, Jwts.SIG.HS256)                            // 使用HS256算法进行签名
			.compact();                                                // 构建并返回最终的JWT字符串
	}
	
	/**
	 * 解析 JWT Token
	 */
	public Claims parseToken(String token)
	{
		try
		{
			return Jwts.parser()
				// .setSigningKey(key)
				.verifyWith(key)
				.build()
				// .parseClaimsJws(token)
				.parseSignedClaims(token)
				// .getBody();
				.getPayload();
		}
		catch (SecurityException | MalformedJwtException e)
		{
			log.error("Invalid JWT signature");
		}
		catch (ExpiredJwtException e)
		{
			log.error("Expired JWT token");
		}
		catch (UnsupportedJwtException e)
		{
			log.error("Unsupported JWT token");
		}
		catch (IllegalArgumentException e)
		{
			log.error("JWT token compact of handler type could not be parsed");
		}
		return null;
	}
	
	/**
	 * 从 Token 中获取用户名（subject）
	 */
	public String getSubjectFromToken(String token)
	{
		Claims claims = parseToken(token);
		return claims != null ? claims.getSubject() : null;
	}
	
	/**
	 * 检查 Token 是否过期
	 */
	public boolean isTokenExpired(String token)
	{
		Claims claims = parseToken(token);
		if (claims == null)
		{
			return true;
		}
		return claims.getExpiration()
			.before(new Date());
	}
}