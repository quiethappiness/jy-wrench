package io.github.quiethappiness.wrench.db.router.config.bean;

import com.zaxxer.hikari.HikariDataSource;
import io.github.quiethappiness.wrench.db.router.config.property.DBRouterProperties;
import io.github.quiethappiness.wrench.db.router.domain.model.DBContextHolder;
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
		String defaultDataSourceKey = null;
		for (Map.Entry<String, DBRouterProperties.WrenchDBRouterDataSourceProperty> dbInfo : dataSourceEnvironment.getDataSourceMap()
			.entrySet())
		{
			String dbInfoKey = dbInfo.getKey();
			DBRouterProperties.WrenchDBRouterDataSourceProperty objMap = dbInfo.getValue();
			
			// 创建数据源并设置驱动类名
			HikariDataSource dataSource = new HikariDataSource();
			dataSource.setDriverClassName(objMap.getDriverClassName());
			dataSource.setJdbcUrl(objMap.getUrl());
			dataSource.setUsername(objMap.getUsername());
			dataSource.setPassword(objMap.getPassword());
			
			
			targetDataSources.put(dbInfoKey, dataSource);
			
			// 设置第一个数据源为默认数据源
			if (defaultDataSourceKey == null) {
				defaultDataSourceKey = dbInfoKey;
			}
		}
		
		// 设置数据源
		this.setTargetDataSources(targetDataSources);
		// 设置默认数据源
		if (defaultDataSourceKey != null) {
			this.setDefaultTargetDataSource(targetDataSources.get(defaultDataSourceKey));
		}
	}
	
	@Override
	protected Object determineCurrentLookupKey()
	{
		return "db" + DBContextHolder.getDBKey();
	}
}