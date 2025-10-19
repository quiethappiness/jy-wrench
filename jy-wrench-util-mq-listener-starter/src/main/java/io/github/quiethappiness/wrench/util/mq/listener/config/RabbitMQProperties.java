package io.github.quiethappiness.wrench.util.mq.listener.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DCCAutoProperties
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置
 * @date 2025/9/10 15:39
 */

@ConfigurationProperties(prefix = "jy.wrench.util.mq-listener", ignoreInvalidFields = true)
// @AutoConfiguration
@Data
public class RabbitMQProperties
{
	private boolean enabled = true;
	private DeadLetter dl = new DeadLetter();
	
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DeadLetter
	{
		private String routing_key;
		private String exchange;
		private String queue;
	}
}