package io.github.quiethappiness.wrench.method.extention.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.github.quiethappiness.wrench.method.extention.config.property.MethodExtensionProperties.JY_WRENCH_CONFIG_METHODE;

@ConfigurationProperties(prefix = JY_WRENCH_CONFIG_METHODE, ignoreInvalidFields = true)
@Data
public class MethodExtensionProperties
{
	public static final String JY_WRENCH_CONFIG_METHODE = "jy.wrench.config.methode";
	private boolean enabled = true;
}