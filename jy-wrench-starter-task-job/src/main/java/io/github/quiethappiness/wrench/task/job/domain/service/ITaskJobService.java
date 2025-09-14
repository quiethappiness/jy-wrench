package io.github.quiethappiness.wrench.task.job.domain.service;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * ITaskJobService
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度服务接口
 * @date 2025/9/13 20:24
 */
public interface ITaskJobService
{
	/**
	 * 刷新任务调度配置
	 */
	void refreshTasks();
	
	/**
	 * 初始化任务调度配置
	 * 在服务启动时加载所有有效的任务调度配置
	 */
	void initializeTasks();
	
	/**
	 * 清理无效任务
	 */
	void cleanInvalidTasks();
	
	/**
	 * 停止所有任务
	 */
	void stopAllTasks();
	
	String getStatus(ScheduledFuture<?> future);
	String getStatus(Long id);
	
	/**
	 * 获取当前活跃任务数量
	 * @return 活跃任务数量
	 */
	int getActiveTaskCount();
	
	/** 查看当前任务状态
	 * @return 当前任务状态
	 */
	Map<String, String> getTasksStatus();
	/**
	 * 添加单个任务
	 * @param task 任务调度配置
	 * @return 是否添加成功
	 */
	boolean addTask(TaskJobScheduleVO task);
	
	/**
	 * 移除单个任务
	 * @param taskId 任务ID
	 * @return 是否移除成功
	 */
	boolean removeTask(Long taskId);
}