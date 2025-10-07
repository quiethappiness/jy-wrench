package io.github.quiethappiness.wrench.method.extention.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jy.wrench.config.methode", ignoreInvalidFields = true)
@Data
public class MethodExtensionProperties
{
	private boolean enabled = true;
}
