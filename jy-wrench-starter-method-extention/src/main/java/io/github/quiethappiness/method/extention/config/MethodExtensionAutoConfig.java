package io.github.quiethappiness.method.extention.config;

import io.github.quiethappiness.method.extention.config.property.MethodExtensionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(MethodExtensionProperties.class)
@Slf4j
@ComponentScan(basePackages = "io.github.quiethappiness.method.extention.config.configuration")
@EnableAspectJAutoProxy
public class MethodExtensionAutoConfig
{
	{
		log.info("MethodExtensionAutoConfig init");
	}
}
