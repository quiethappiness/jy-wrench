package io.github.quiethappiness.db.router.config.configuration;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component("dbRouterConfig")
public class DBRouterConfig
{
	private int dbCount;  //分库数
	private int tbCount;  //分表数
	
	public DBRouterConfig(
		DataSourceEnvironment dataSourceEnvironment)
	{
		this.dbCount = dataSourceEnvironment.getDbCount();
		this.tbCount = dataSourceEnvironment.getTbCount();
	}
}
