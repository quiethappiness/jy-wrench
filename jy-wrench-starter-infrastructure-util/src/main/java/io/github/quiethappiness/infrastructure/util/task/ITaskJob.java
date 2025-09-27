package io.github.quiethappiness.infrastructure.util.task;

import java.util.List;
import java.util.Map;

/**
 * ITaskJob
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 处理回调任务
 * @date 2025/9/17 17:11
 */
public interface ITaskJob<T>
{
	
	void execJobAsync(T notifyTaskEntity) throws Exception;
	
	/**
	 * 执行回调通知完成结算
	 * @param notifyTaskEntityList
	 * @return
	 * @throws Exception
	 */
	Map<String, Integer> execJob(List<T> notifyTaskEntityList) throws Exception;
}