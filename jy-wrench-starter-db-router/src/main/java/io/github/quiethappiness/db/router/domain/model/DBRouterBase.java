package io.github.quiethappiness.db.router.domain.model;

public class DBRouterBase
{
	private String tbIdx;
	
	public String getTbIdx() {
		return DBContextHolder.getTBKey();
	}
}
