package io.github.quiethappiness.wrench.util.redisson.domain.base.structure;

import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamCreateGroupArgs;

import java.util.Map;

public interface IRedisStream
{
	/**
	 * 一个stream相当于一个exchange,而且是广播模式，会发给所有的queue（group）
	 * <p>获取Redis Stream
	 * @param streamName stream名称
	 * @return Redis Stream
	 * @param <K> key类型
	 * @param <V> value类型
	 */
	<K, V> RStream<K, V> getStream(String streamName);
	
	<K,V>Map<StreamMessageId, Map<K, V>> range(String streamName,StreamMessageId startId, StreamMessageId endId);
	/**
	 * 一个分组相当于一个queue
	 * <p>创建Redis Stream的分组
	 * @param streamName stream名称
	 * @param args 创建参数
	 */
	void createGroup(String streamName,StreamCreateGroupArgs args);
	/**
	 * 一个消费者相当于一个queue的消费者实例
	 * <p>创建Redis Stream的消费者
	 * @param streamName stream名称
	 * @param groupName 分组名称
	 * @param consumerName 消费者名称
	 */
	void createConsumer(String streamName,String groupName, String consumerName);
	void updateGroupMessageId(String streamName,String groupName, StreamMessageId id);
	long ack(String streamName,String groupName, StreamMessageId... ids);
}
