package io.github.quiethappiness.wrench.util.redisson.domain.inter;

import org.redisson.api.RLexSortedSet;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSortedSet;

public interface IRedisZSet
{
	/**
	 * 将指定的值添加到有序集合中
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 */
	void addToSortedSet(String key, String value);
	
	/**
	 * 元素的自然顺序（需实现Comparable接口或提供Comparator）
	 * <p>1. 在客户端进行排序2. 数据量较小时使用
	 * <p>需要按对象自身属性（如名称、日期）排序的小规模集合
	 * @param name
	 * @return
	 * @param <V>
	 */
	<V> RSortedSet<V> getSortedSet(String name);
	
	/**
	 * 显式指定的分数（Score）（一个double类型数值）
	 * <p>1. 服务端排序，性能极高2. 支持灵活的范围查询3. 可动态调整元素分数
	 * <p>排行榜、优先级队列、时间轴、价格区间筛选
	 * @param name
	 * @return
	 * @param <V>
	 */
	<V> RScoredSortedSet<V> getScoredSortedSet(String name);
	
	/**
	 * 元素的字典顺序（Lexicographical Order）
	 * <p>1. 专为字符串字典序排序优化2. 提供高效的字母范围查询
	 * <p>电话号码段、用户名前缀检索、按字母顺序排列的字符串集合
	 * @param name
	 * @return
	 */
	RLexSortedSet getLexSortedSet(String name);
}