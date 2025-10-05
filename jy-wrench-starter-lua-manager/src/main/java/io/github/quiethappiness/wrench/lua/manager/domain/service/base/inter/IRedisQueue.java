package io.github.quiethappiness.wrench.lua.manager.domain.service.base.inter;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;

public interface IRedisQueue
{
	/**
	 * 获取队列
	 * @param key
	 * 	键
	 * @param <T>
	 * 	泛型
	 * @return 队列
	 */
	<T> RQueue<T> getQueue(String key);
	
	/**
	 * 加锁队列
	 * @param key
	 * 	键
	 * @param <T>
	 * 	泛型
	 * @return 队列
	 */
	<T> RBlockingQueue<T> getBlockingQueue(String key);
	
	/**
	 * 延迟队列
	 * @param rBlockingQueue
	 * 	加锁队列
	 * @param <T>
	 * 	泛型
	 * @return 队列
	 */
	<T> RDelayedQueue<T> getDelayedQueue(RBlockingQueue<T> rBlockingQueue);
	
}
