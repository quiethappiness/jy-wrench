package io.github.quiethappiness.db.router.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Deprecated
@ConfigurationProperties(prefix = "jy.wrench.db-router.jdbc.datasource", ignoreInvalidFields = true)
@Data
public class DBRouterProperties
{
	private boolean enabled = true;
	private List<String> list;
}
