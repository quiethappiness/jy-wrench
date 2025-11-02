package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import lombok.Builder;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * WhiteListVO
 * @description whiteList方法参数与返回值VO
 * @author quietHappiness @jingyue
 * @date 2025/11/2 10:23
 * @version 1.0
 */
public record WhiteListVO()
{
	@Builder
	public record WhiteListResultEntity(
		AbstractWhiteListSupport.InWhitListResult inWhitListResult)
	{
	}
	
	@Builder
	public record WhiteListParameterEntity(
		TcWhiteList tcWhiteList,
		ProceedingJoinPoint jp,
		WhiteListProperties whiteListProperties)
	{
	}
}