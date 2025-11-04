package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * IIdempotentCheck
 * @description
 * @author quietHappiness @jingyue
 * @date 2025/11/3 10:04
 * @version 1.0
 */

public interface IIdempotentCheck
{
	
	boolean isRequestTooFrequent(String businessType, TcIdempotent tcIdempotent,String mark);
	
	boolean hasSimilarRecentRequest(ProceedingJoinPoint joinPoint, String businessType) throws NoSuchMethodException;
}