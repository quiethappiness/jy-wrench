package io.github.quiethappiness.db.router.domain.service;

import io.github.quiethappiness.aop.util.WrenchAopUtil;
import io.github.quiethappiness.db.router.config.configuration.DBRouterConfig;
import io.github.quiethappiness.db.router.domain.model.DBContextHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.annotation.Resource;

public abstract class AbstractDBRouterAOP implements IDBRouterAOP
{
	@Resource
	protected DBRouterConfig dbRouterConfig;
	
	protected CalRouterResult doCalRouter(ProceedingJoinPoint jp, String dbKey)
	{
		String dbKeyAttr = WrenchAopUtil.getAttrValue(dbKey, jp.getArgs());
		// 检查键值是否为空
		if (dbKeyAttr == null)
		{
			throw new IllegalArgumentException("数据库路由键不能为空");
		}
		int dbCount = dbRouterConfig.getDbCount();
		int tbCount = dbRouterConfig.getTbCount();
		int size = dbCount * tbCount;
		// 改进的哈希计算：使用绝对值确保非负，并增加分布均匀性
		int hashCode = dbKeyAttr.hashCode();
		int perturbedHash = hashCode ^ (hashCode >>> 16);
		// 扰动函数
		int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));
		// 库表索引
		int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
		int tbIdx = Math.abs(idx - dbRouterConfig.getTbCount() * (dbIdx - 1)) % tbCount + 1;
		// 验证索引范围
		if (dbIdx > dbCount || tbIdx > tbCount)
		{
			throw new IllegalStateException("计算出的索引超出范围: dbIdx=" + dbIdx + ", tbIdx=" + tbIdx);
		}
		// 设置到 ThreadLocal
		DBContextHolder.setDBKey(String.format("%02d", dbIdx));
		DBContextHolder.setTBKey(String.format("%02d", tbIdx));
		return new CalRouterResult(dbIdx, tbIdx);
	}
	
	@AllArgsConstructor
	@Data
	protected static class CalRouterResult
	{
		public final int dbIdx;
		public final int tbIdx;
	}
}
