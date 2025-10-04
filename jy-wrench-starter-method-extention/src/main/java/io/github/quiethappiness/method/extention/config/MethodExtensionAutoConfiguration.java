package io.github.quiethappiness.method.extention.config;

import io.github.quiethappiness.method.extention.config.configuration.MethodExtensionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(MethodExtensionConfiguration.class)
public class MethodExtensionAutoConfiguration
{

}
