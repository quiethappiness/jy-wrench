package io.github.quiethappiness.wrench.db.router.config.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "jy.wrench.db-router.jdbc.datasource", ignoreInvalidFields = true)
@Data
public class DBRouterProperties
{
	private boolean enabled = true;
	private int dbCount;
	private int tbCount;
	private Map<String, WrenchDBRouterDataSourceProperty> map;
	
	@AllArgsConstructor
	@Data
	@Builder
	public static class WrenchDBRouterDataSourceProperty
	{
		private String driverClassName;
		private String url;
		private String username;
		private String password;
	}
}