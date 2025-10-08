package io.github.quiethappiness.wrench.task.job.domain.service.impl;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;
import io.github.quiethappiness.wrench.task.job.domain.service.ITaskJobService;
import io.github.quiethappiness.wrench.task.job.provider.ITaskDataProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * AbstractTaskJobService
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象实现
 * @date 2025/9/13 21:12
 */
@Slf4j
public abstract class AbstractTaskJobService implements ITaskJobService
{
	protected List<ITaskDataProvider> taskDataProviders;
	/**
	 * 任务ID与任务执行器的映射，用于记录已添加的任务
	 */
	protected Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
	
	@Override
	public void initializeTasks()
	{
		log.info("开始初始化任务调度配置");
		try
		{
			// 聚合所有数据提供者的任务调度配置
			List<TaskJobScheduleVO> allTaskSchedules = new ArrayList<>();
			taskDataProviders.forEach(provider ->
			{
				List<TaskJobScheduleVO> scheduleVOS = provider.queryAllValidTaskSchedule();
				if (!scheduleVOS.isEmpty())
				{
					allTaskSchedules.addAll(scheduleVOS);
				}
			});
			// 处理每个任务调度配置
			// 创建并调度新任务
			allTaskSchedules.forEach(this::scheduleTask);
			log.info("任务调度配置初始化完成，已加载任务数: {}", scheduledTasks.size());
		}
		catch (Exception e)
		{
			log.error("初始化任务调度配置时发生错误", e);
		}
	}
	
	@Override
	public void cleanInvalidTasks()
	{
		log.info("开始清理无效的任务");
		try
		{
			// 聚合所有数据提供者的无效任务ID
			List<Long> allInvalidTaskIds = new ArrayList<>();
			taskDataProviders.forEach(provider ->
			{
				List<Long> invalidTaskIds = provider.queryAllInvalidTaskScheduleIds();
				if (invalidTaskIds != null)
				{
					allInvalidTaskIds.addAll(invalidTaskIds);
				}
			});
			if (allInvalidTaskIds.isEmpty())
			{
				log.info("没有发现无效的任务需要清理");
				return;
			}
			log.info("发现{}个无效任务需要清理", allInvalidTaskIds.size());
			// 从调度器中移除这些任务
			allInvalidTaskIds.forEach(taskId ->
			{
				ScheduledFuture<?> future = scheduledTasks.remove(taskId);
				if (future != null)
				{
					future.cancel(true);
					log.info("已移除无效任务，ID: {}", taskId);
				}
			});
			log.info("无效任务清理完成，当前活跃任务数: {}", scheduledTasks.size());
		}
		catch (Exception e)
		{
			log.error("清理无效任务时发生错误", e);
		}
	}
	
	@Override
	public void stopAllTasks()
	{
		log.info("开始停止所有任务");
		scheduledTasks.forEach((id, future) ->
		{
			if (future != null)
			{
				future.cancel(true);
				log.info("已取消任务，ID: {}", id);
			}
		});
		scheduledTasks.clear();
		log.info("所有任务已停止");
	}
	
	@Override
	public Map<String, String> getTasksStatus()
	{
		log.info("获取任务状态");
		Map<String, String> taskStatus = new ConcurrentHashMap<>();
		scheduledTasks.forEach((id, future) ->
		{
			String status = getStatus(future);
			taskStatus.put(id.toString(), status);
		});
		return taskStatus;
	}
	
	@Override
	public String getStatus(ScheduledFuture<?> future)
	{
		String status = null;
		if (future != null)
		{
			if (future.isDone())
			{
				status = "已完成";
			}
			else if (future.isCancelled())
			{
				status = "已取消";
			}
			else
			{
				status = "正在执行";
			}
		}
		else
		{
			status = "未开始或不存在";
		}
		return status;
	}
	
	@Override
	public String getStatus(Long id)
	{
		return getStatus(scheduledTasks.get(id));
	}
	
	@Override
	public int getActiveTaskCount()
	{
		return scheduledTasks.size();
	}
	
	@Override
	public void refreshTasks()
	{
		log.info("开始刷新任务调度配置,当前活跃任务数: {}", scheduledTasks.size());
		try
		{
			// 聚合所有数据提供者的任务调度配置
			List<TaskJobScheduleVO> allTaskSchedules = new ArrayList<>();
			taskDataProviders.forEach(provider ->
			{
				List<TaskJobScheduleVO> scheduleVOS = provider.queryAllValidTaskSchedule();
				if (!scheduleVOS.isEmpty())
				{
					allTaskSchedules.addAll(scheduleVOS);
				}
			});
			// 记录当前配置中的任务ID
			Map<Long, Boolean> currentTaskIds = new ConcurrentHashMap<>();
			// 处理每个任务调度配置
			allTaskSchedules.forEach(task ->
			{
				Long taskId = task.getId();
				currentTaskIds.put(taskId, true);
				// 如果任务已经存在，则跳过
				if (!scheduledTasks.containsKey(taskId))
				{
					// 创建并调度新任务
					scheduleTask(task);
				}
			});
			// 移除已不存在的任务
			scheduledTasks.keySet()
				.removeIf(taskId ->
				{
					if (!currentTaskIds.containsKey(taskId))
					{
						ScheduledFuture<?> future = scheduledTasks.remove(taskId);
						if (future != null)
						{
							future.cancel(true);
							log.info("已移除任务，ID: {}", taskId);
						}
						return true;
					}
					return false;
				});
			log.info("任务调度配置刷新完成，当前活跃任务数: {}", scheduledTasks.size());
		}
		catch (Exception e)
		{
			log.error("刷新任务调度配置时发生错误", e);
		}
	}
	
	@Override
	public boolean addTask(TaskJobScheduleVO task)
	{
		try
		{
			if (task == null || task.getId() == null)
			{
				log.warn("任务配置为空或任务ID为空，无法添加任务");
				return false;
			}
			// 如果任务已存在，先移除旧任务
			if (scheduledTasks.containsKey(task.getId()))
			{
				log.info("任务已存在，先移除旧任务，ID: {}", task.getId());
				removeTask(task.getId());
			}
			// 调度新任务
			scheduleTask(task);
			log.info("任务添加成功，ID: {}, 描述: {}", task.getId(), task.getDescription());
			return true;
		}
		catch (Exception e)
		{
			log.error("添加任务时发生错误，ID: {}", task != null ? task.getId() : "null", e);
			return false;
		}
	}
	
	@Override
	public boolean removeTask(Long taskId)
	{
		try
		{
			if (taskId == null)
			{
				log.warn("任务ID为空，无法移除任务");
				return false;
			}
			ScheduledFuture<?> future = scheduledTasks.remove(taskId);
			if (future != null)
			{
				future.cancel(true);
				log.info("任务移除成功，ID: {}", taskId);
				return true;
			}
			else
			{
				log.warn("未找到要移除的任务，ID: {}", taskId);
				return false;
			}
		}
		catch (Exception e)
		{
			log.error("移除任务时发生错误，ID: {}", taskId, e);
			return false;
		}
	}
	
	protected abstract void scheduleTask(TaskJobScheduleVO task);
	
	protected abstract void executeTaskWithFunction(TaskJobScheduleVO task);
}