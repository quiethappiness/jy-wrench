package io.github.quiethappiness.db.router.config.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConditionalOnProperty(name = "jy.wrench.db-router.enabled", havingValue = "true", matchIfMissing = false)
@ConfigurationProperties(prefix = "jy.wrench.db-router", ignoreInvalidFields = true)
@Data
public class DBRouterProperties
{
	private boolean enabled = true;
}
