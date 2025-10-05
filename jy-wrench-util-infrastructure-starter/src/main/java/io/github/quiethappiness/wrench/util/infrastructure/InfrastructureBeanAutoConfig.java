package io.github.quiethappiness.wrench.util.infrastructure;

import io.github.quiethappiness.wrench.util.infrastructure.gateway.CommonRequestService;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ComponentScan(basePackages = "io.github.quiethappiness.infrastructure.util")
public class InfrastructureBeanAutoConfig
{
	@Bean("commonRequestService")
	@ConditionalOnBean(value = {OkHttpClient.class})
	public CommonRequestService commonRequestService()
	{
		return new CommonRequestService();
	}
}
