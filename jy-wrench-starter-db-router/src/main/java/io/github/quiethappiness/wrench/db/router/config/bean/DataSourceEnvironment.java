package io.github.quiethappiness.wrench.db.router.config.bean;

import io.github.quiethappiness.wrench.db.router.config.property.DBRouterProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Getter
@Slf4j
public class DataSourceEnvironment implements EnvironmentAware
{
	public final String prefix = "jy.wrench.config.db-router.jdbc.datasource.";
	private final Map<String, DBRouterProperties.WrenchDBRouterDataSourceProperty> dataSourceMap = new HashMap<>();
	private int dbCount;    //分库数
	private int tbCount;    //分表数
	
	@Override
	public void setEnvironment(Environment environment)
	{
		log.info("正在加载数据源配置...");
		dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
		tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
		// 使用 Binder 获取整个 map 配置
		Binder binder = Binder.get(environment);
		Map<String, DBRouterProperties.WrenchDBRouterDataSourceProperty> allDataSources = binder.bind(prefix + "map", Bindable.mapOf(String.class, DBRouterProperties.WrenchDBRouterDataSourceProperty.class))
			.orElse(new HashMap<>());
		// 将所有数据源配置放入 dataSourceMap
		dataSourceMap.putAll(allDataSources);
		log.info("数据源配置加载完成...dbCount={}, tbCount={}, dataSourceMap={}", dbCount, tbCount, dataSourceMap);
	}
}
