package io.github.quiethappiness.wrench.rate.limiter.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RequestParameterEntity
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 请求参数
 * @date 2025/9/12 18:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestParameterEntity
{
	private String rateLimiterSwitch;
	
}