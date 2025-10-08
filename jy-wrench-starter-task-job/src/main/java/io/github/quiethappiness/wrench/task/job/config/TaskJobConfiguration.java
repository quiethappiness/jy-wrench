package io.github.quiethappiness.wrench.task.job.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * TaskJobConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度器配置
 * @date 2025/9/13 20:30
 */
@ConditionalOnProperty(prefix = "jy.wrench.config.task.job", name = "enabled", havingValue = "true", matchIfMissing = false)
@AutoConfiguration
@EnableConfigurationProperties(value = {TaskJobProperties.class})
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.task.job.domain.service"})
@EnableScheduling
public class TaskJobConfiguration
{
	private final Logger log = LoggerFactory.getLogger(TaskJobConfiguration.class);
	{
		log.info("已启用 task job 管理功能");
	}
	/**
	 * 创建线程池任务调度器实例，用于执行定时任务和异步任务调度
	 */
	@Bean("jyWrenchTaskScheduler")
	public TaskScheduler taskScheduler(
		@Autowired TaskJobProperties properties)
	{
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(properties.getPoolSize());
		scheduler.setThreadNamePrefix(properties.getThreadNamePrefix());
		scheduler.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksToCompleteOnShutdown());
		scheduler.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
		scheduler.initialize();
		log.warn("jy-wrench，任务调度器(jyWrenchTaskScheduler)初始化完成。线程池大小: {}, 线程名前缀: {}",
			properties.getPoolSize(), properties.getThreadNamePrefix());
		return scheduler;
	}
	
	// @Bean
	// public ITaskJobService taskJobService(
	// 	@Autowired @Qualifier("jyWrenchTaskScheduler") TaskScheduler jyWrenchTaskScheduler,
	// 	@Autowired List<ITaskDataProvider> taskDataProviders)
	// {
	// 	// 将任务执行器列表转换为Map，以执行器名称为key
	// 	// Map<String, ITaskExecutor> executorMap = taskExecutors.stream()
	// 	// 	.collect(Collectors.toMap(ITaskExecutor::getExecutorName, Function.identity()));
	// 	// log.info("jy-wrench，任务调度服务初始化完成。注册的任务执行器: {}", executorMap.keySet());
	// 	// 实例化任务并初始化调度
	// 	ITaskJobService taskJobService = new TaskJobServiceImpl(jyWrenchTaskScheduler, taskDataProviders);
	// 	// taskJobService.initializeTasks();
	// 	log.info("jy-wrench，任务调度服务(taskJobService)初始化完成。已加载任务数: {}", taskJobService.getActiveTaskCount());
	// 	return taskJobService;
	// }
	
	/**
	 * 自动检测任务
	 */
	// @Bean
	// public TaskJobManager taskJob(
	// 	@NonNull @Autowired TaskJobProperties properties,
	// 	@Autowired ITaskJobService taskJobService)
	// {
	// 	log.info("jy-wrench，任务调度作业(TaskJobManager)初始化完成。刷新间隔: {}ms, 清理cron: {}",
	// 		properties.getRefreshInterval(), properties.getCleanInvalidTasksCron());
	// 	return new TaskJobManager(properties, taskJobService);
	// }
}