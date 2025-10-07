package io.github.quiethappiness.wrench.dynamic.config.center.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * DCCAutoConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@AutoConfiguration
@Import({DCCRegisterConfiguration.class})
public class DCCAutoConfiguration
{
}