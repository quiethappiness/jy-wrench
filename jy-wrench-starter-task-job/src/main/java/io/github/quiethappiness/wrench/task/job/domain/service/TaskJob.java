package io.github.quiethappiness.wrench.task.job.domain.service;

import io.github.quiethappiness.wrench.task.job.config.TaskJobAutoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * TaskJob
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度作业：定时获取有效的任务调度配置，并动态创建新的任务
 * @date 2025/9/13 20:28
 */
@Slf4j
public class TaskJob
{
	private final TaskJobAutoProperties properties;
	private final ITaskJobService taskJobService;
	
	public TaskJob(TaskJobAutoProperties properties, ITaskJobService taskJobService)
	{
		this.properties = properties;
		this.taskJobService = taskJobService;
	}
	
	/**
	 * 定时刷新任务调度配置
	 */
	@Scheduled(fixedRateString = "${jy.wrench.task.job.refresh-interval:60000}")
	public void refreshTasks()
	{
		if (!properties.isEnabled())
		{
			return;
		}
		taskJobService.refreshTasks();
	}
	
	/**
	 * 定时清理无效任务
	 */
	@Scheduled(cron = "${jy.wrench.task.job.clean-invalid-tasks-cron:0 0/10 * * * ?}")
	public void cleanInvalidTasks()
	{
		if (!properties.isEnabled())
		{
			return;
		}
		taskJobService.cleanInvalidTasks();
	}
}