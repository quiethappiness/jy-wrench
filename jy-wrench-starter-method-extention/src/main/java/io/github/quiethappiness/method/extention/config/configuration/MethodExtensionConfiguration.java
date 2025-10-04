package io.github.quiethappiness.method.extention.config.configuration;

import io.github.quiethappiness.method.extention.config.condition.OnMethodExtensionCondition;
import io.github.quiethappiness.method.extention.config.property.MethodExtensionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Conditional(OnMethodExtensionCondition.class) // 核心：应用“或”逻辑条件
@ComponentScan("io.github.quiethappiness.method.extention.domain") // 扫描指定包下的类
@EnableConfigurationProperties(MethodExtensionProperties.class) // 引入配置类
@Slf4j
@EnableAspectJAutoProxy
@Configuration
public class MethodExtensionConfiguration
{
}
