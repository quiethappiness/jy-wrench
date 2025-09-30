package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RateLimiterReturnResultEntity
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 返回值
 * @date 2025/9/12 19:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimiterReturnResultEntity
{
	private boolean decideLimit;
}