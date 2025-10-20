package io.github.quiethappiness.wrench.db.router.config.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class DBRouterProperty
{
	private int dbCount;  //分库数
	private int tbCount;  //分表数
	
	public DBRouterProperty(
		DataSourceEnvironment dataSourceEnvironment)
	{
		this.dbCount = dataSourceEnvironment.getDbCount();
		this.tbCount = dataSourceEnvironment.getTbCount();
	}
}
