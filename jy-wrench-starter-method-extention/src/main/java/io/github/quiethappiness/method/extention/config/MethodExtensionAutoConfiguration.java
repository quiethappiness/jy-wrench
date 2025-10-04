package io.github.quiethappiness.method.extention.config;

import io.github.quiethappiness.method.extention.config.configuration.MethodExtensionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;


@Configuration
@EnableAspectJAutoProxy
@Import(MethodExtensionConfiguration.class)
public class MethodExtensionAutoConfiguration
{

}
