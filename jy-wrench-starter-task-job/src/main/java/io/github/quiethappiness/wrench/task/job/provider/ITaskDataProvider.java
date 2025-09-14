package io.github.quiethappiness.wrench.task.job.provider;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;

import java.util.List;

/**
 * ITaskDataProvider
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务数据提供者接口
 * @date 2025/9/13 20:23
 */
public interface ITaskDataProvider
{
	/**
	 * 查询所有有效的任务调度配置
	 * @return 任务调度配置列表
	 */
	List<TaskJobScheduleVO> queryAllValidTaskSchedule();
	
	/**
	 * 查询所有无效的任务ID
	 * @return 无效任务ID列表
	 */
	List<Long> queryAllInvalidTaskScheduleIds();
}