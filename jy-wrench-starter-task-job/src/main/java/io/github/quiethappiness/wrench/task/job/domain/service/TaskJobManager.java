package io.github.quiethappiness.wrench.task.job.domain.service;

import io.github.quiethappiness.wrench.task.job.config.TaskJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * TaskJobManager
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度作业：定时获取有效的任务调度配置，并动态创建新的任务
 * @date 2025/9/13 20:28
 */
@Slf4j
@Service
public class TaskJobManager
{
	private final TaskJobProperties properties;
	private final ITaskJobService taskJobService;
	
	public TaskJobManager(
		@NonNull @Autowired TaskJobProperties properties,
		@Autowired ITaskJobService taskJobService)
	{
		this.properties = properties;
		this.taskJobService = taskJobService;
		log.info("jy-wrench，任务调度作业(TaskJobManager)初始化完成。刷新间隔: {}ms, 清理cron: {}",
			properties.getRefreshInterval(), properties.getCleanInvalidTasksCron());
	}
	
	/**
	 * 定时刷新任务调度配置
	 */
	@Scheduled(fixedRateString = "${jy.wrench.config.task.job.refresh-interval:60000}")
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
	@Scheduled(cron = "${jy.wrench.config.task.job.clean-invalid-tasks-cron:0 0/10 * * * ?}")
	public void cleanInvalidTasks()
	{
		if (!properties.isEnabled())
		{
			return;
		}
		taskJobService.cleanInvalidTasks();
	}
}