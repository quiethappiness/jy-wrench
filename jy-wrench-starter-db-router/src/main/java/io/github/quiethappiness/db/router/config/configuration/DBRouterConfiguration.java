package io.github.quiethappiness.db.router.config.configuration;

import io.github.quiethappiness.db.router.domain.model.DBContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "jy.wrench.db-router.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
@ComponentScan("io.github.quiethappiness.db.router.domain")
public class DBRouterConfiguration implements EnvironmentAware
{
	@Override
	public void setEnvironment(Environment environment)
	{
	}
	
	private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();
	private int dbCount;    //分库数
	private int tbCount;    //分表数
	
	@Bean
	public DBRouterConfig dbRouterConfig()
	{
		return new DBRouterConfig(dbCount, tbCount);
	}
	
	@Bean
	public DataSource dataSource()
	{
//  创建数据源
		Map<Object, Object> targetDataSources = new HashMap<>();
		for (String dbInfo : dataSourceMap.keySet())
		{
			Map<String, Object> objMap = dataSourceMap.get(dbInfo);
			targetDataSources.put(dbInfo, new DriverManagerDataSource(objMap.get("url")
				.toString(), objMap.get("username")
				.toString(), objMap.get("password")
				.toString()));
		}
//  设置数据源                
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setTargetDataSources(targetDataSources);
		return dynamicDataSource;
	}
	
	@Override
	public void setEnvironment(Environment environment)
	{
		String prefix = "router.jdbc.datasource.";
		dbCount = Integer.valueOf(environment.getProperty(prefix + "dbCount"));
		tbCount = Integer.valueOf(environment.getProperty(prefix + "tbCount"));
		String dataSources = environment.getProperty(prefix + "list");
		for (String dbInfo : dataSources.split(","))
		{
			Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
			dataSourceMap.put(dbInfo, dataSourceProps);
		}
	}
	
	/**
	 * 这里的动态数据源需要继承 AbstractRoutingDataSource 实现 determineCurrentLookupKey 方法。
	 * •在这个方法中使用 DBContextHolder.getDBKey()，获取路由设置到 ThreadLocal 的结果。
	 */
	public static class DynamicDataSource extends AbstractRoutingDataSource
	{
		@Override
		protected Object determineCurrentLookupKey()
		{
			return "db" + DBContextHolder.getDBKey();
		}
	}
	
	private static class DBRouterConfig
	{
		public DBRouterConfig(int dbCount, int tbCount)
		{
			DBRouterBase.dbCount = dbCount;
			DBRouterBase.tbCount = tbCount;
		}
	}
}
