package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent;

import io.github.quiethappiness.wrench.traffic.control.domain.service.IIdempotentAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentCheck;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentToken;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractRateLimiterAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象实现
 * @date 2025/9/12 15:54
 */
@Slf4j
public abstract class AbstractIdempotentAOP implements IIdempotentAOP
{
	@Resource
	protected IIdempotentToken idempotentToken;
	@Resource
	protected IIdempotentCheck idempotentCheck;

	
	
}