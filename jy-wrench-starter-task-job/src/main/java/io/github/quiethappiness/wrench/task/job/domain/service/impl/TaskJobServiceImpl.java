package io.github.quiethappiness.wrench.task.job.domain.service.impl;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;
import io.github.quiethappiness.wrench.task.job.provider.ITaskDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * TaskJobServiceImpl
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度服务实现类
 * @date 2025/9/13 20:25
 */
@Slf4j
@Service
public class TaskJobServiceImpl extends AbstractTaskJobService implements DisposableBean
{
	private final TaskScheduler taskScheduler;
	
	public TaskJobServiceImpl(
		@Autowired @Qualifier("jyWrenchTaskScheduler") TaskScheduler jyWrenchTaskScheduler,
		@Autowired List<ITaskDataProvider> taskDataProviders
	)
	{
		this.taskScheduler = jyWrenchTaskScheduler;
		super.taskDataProviders = taskDataProviders;
		super.initializeTasks();
		log.info("jy-wrench，任务调度服务(taskJobService)初始化完成。已加载任务数: {}", this.getActiveTaskCount());
	}
	
	/**
	 * 调度单个任务
	 */
	@Override
	protected void scheduleTask(TaskJobScheduleVO task)
	{
		try
		{
			log.info("开始调度任务，ID: {}, 描述: {}, Cron表达式: {}", task.getId(), task.getDescription(), task.getCronExpression());
			// 使用新的函数式编程方式
			ScheduledFuture<?> future = taskScheduler.schedule(
				() -> executeTaskWithFunction(task),
				new CronTrigger(task.getCronExpression())
			);
			scheduledTasks.put(task.getId(), future);
			log.info("任务调度成功（函数式），ID: {}", task.getId());
		}
		catch (Exception e)
		{
			log.error("调度任务时发生错误，ID: {}", task.getId(), e);
		}
	}
	
	/**
	 * 使用函数式编程方式执行任务
	 */
	@Override
	protected void executeTaskWithFunction(TaskJobScheduleVO task)
	{
		try
		{
			log.info("开始执行任务（函数式），ID: {}, 描述: {}", task.getId(), task.getDescription());
			// 获取并执行任务
			Runnable taskRunnable = task.getTaskExecutor()
				.get();
			taskRunnable.run();
			log.info("任务执行完成（函数式），ID: {}", task.getId());
		}
		catch (Exception e)
		{
			log.error("执行任务时发生错误（函数式），ID: {}", task.getId(), e);
		}
	}
	
	@Override
	public void destroy()
	{
		stopAllTasks();
	}
}