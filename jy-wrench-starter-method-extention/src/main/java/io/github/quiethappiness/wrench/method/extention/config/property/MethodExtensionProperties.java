package io.github.quiethappiness.wrench.method.extention.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.github.quiethappiness.wrench.method.extention.config.condition.OnMePropertyCondition.PROPERTY_PREFIX;

@ConfigurationProperties(prefix = PROPERTY_PREFIX, ignoreInvalidFields = true)
@Data
public class MethodExtensionProperties
{
	private boolean enabled = true;
}
