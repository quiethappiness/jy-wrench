package io.github.quiethappiness.db.router.config.configuration;

import io.github.quiethappiness.db.router.types.util.PropertyUtil;
import lombok.Getter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class DataSourceEnvironment implements EnvironmentAware
{
	public final String prefix = "jy.wrench.db-router.jdbc.datasource.";
	@Getter
	private final Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();
	@Getter
	private int dbCount;    //分库数
	@Getter
	private int tbCount;    //分表数
	
	@Override
	public void setEnvironment(Environment environment)
	{
		dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
		tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
		String dataSources = environment.getProperty(prefix + "list");
		for (String dbInfo : dataSources.split(","))
		{
			Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
			dataSourceMap.put(dbInfo, dataSourceProps);
		}
	}
}
