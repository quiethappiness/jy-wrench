package io.github.quiethappiness.wrench.util.redisson.domain.base.func;

import org.redisson.api.RTimeSeries;
import org.redisson.api.TimeSeriesEntry;

import java.util.Collection;

public interface IRedisTimeSeries
{
	/**
	 * Returns time-series instance by <code>name</code>
	 * @param <V>
	 * 	value type
	 * @param <L>
	 * 	label type
	 * @param name
	 * 	name of instance
	 * @return RTimeSeries object
	 * 	<p/>举例
	 * 	<p> // 数据内容：
	 * 	<p>// 时间戳(毫秒)        | 价格(值V) | 标签(L)
	 * 	<p>// 1704067200000 (2024-01-01 09:30:00) | 182.50 | "open"
	 * 	<p>// 1704067260000 (2024-01-01 09:31:00) | 182.75 | "trade"
	 * 	<p>// 1704067320000 (2024-01-01 09:32:00) | 183.10 | "trade"
	 * 	<p>// 1704067380000 (2024-01-01 09:33:00) | 182.90 | "trade"
	 * 	<p>// 1704070800000 (2024-01-01 10:30:00) | 185.20 | "high"
	 */
	<V, L> RTimeSeries<V, L> getTimeSeries(String name);
	
	/**
	 * Returns ordered entries of this time-series collection within timestamp range. Including boundary values.
	 * @param startTimestamp
	 * 	- start timestamp
	 * @param endTimestamp
	 * 	- end timestamp
	 * @return elements collection
	 * <p>
	 *     <p> // 数据内容：
	 *     <p>// 查询结果：
	 * [
	 *    {timestamp: 1704067200000, value: 182.50, label: "open"},
	 *    {timestamp: 1704067260000, value: 182.75, label: "trade"},
	 *    {timestamp: 1704067320000, value: 183.10, label: "trade"}
	 * ]
	 */
	<V,L> Collection<TimeSeriesEntry<V, L>> entryRange(String name,long startTimestamp, long endTimestamp);
	
	<V,L> Collection<TimeSeriesEntry<V, L>> entryRange(String name,long startTimestamp, long endTimestamp,int limit);
}