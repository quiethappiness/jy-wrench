package io.github.quiethappiness.method.extention.config.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(name = "jy.wrench.methode.enabled", havingValue = "true",matchIfMissing = false)
@ConfigurationProperties(prefix = "jy.wrench.methode", ignoreInvalidFields = true)
@Data
public class MethodExtensionProperties
{
	private boolean enabled = true;
}
