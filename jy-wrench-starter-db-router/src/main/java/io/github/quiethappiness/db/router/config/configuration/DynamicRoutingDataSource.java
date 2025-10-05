package io.github.quiethappiness.db.router.config.configuration;

import io.github.quiethappiness.db.router.config.property.DBRouterProperties;
import io.github.quiethappiness.db.router.domain.model.DBContextHolder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里的动态数据源需要继承 AbstractRoutingDataSource 实现 determineCurrentLookupKey 方法。
 * •在这个方法中使用 DBContextHolder.getDBKey()，获取路由设置到 ThreadLocal 的结果。
 */
@Component
public class DynamicRoutingDataSource extends AbstractRoutingDataSource
{
	public DynamicRoutingDataSource(
		DataSourceEnvironment dataSourceEnvironment)
	{
		//  创建数据源
		Map<Object, Object> targetDataSources = new HashMap<>();
		for (Map.Entry<String, DBRouterProperties.WrenchDBRouterDataSourceProperty> dbInfo : dataSourceEnvironment.getDataSourceMap()
			.entrySet())
		{
			String dbInfoKey = dbInfo.getKey();
			DBRouterProperties.WrenchDBRouterDataSourceProperty objMap = dbInfo.getValue();
			targetDataSources.put(
				dbInfoKey,
				new DriverManagerDataSource(objMap.getUrl(), objMap.getUsername(), objMap.getPassword())
			);
		}
		//  设置数据源
		this.setTargetDataSources(targetDataSources);
	}
	
	@Override
	protected Object determineCurrentLookupKey()
	{
		return "db" + DBContextHolder.getDBKey();
	}
}
