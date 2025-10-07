package io.github.quiethappiness.wrench.task.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TaskJobAutoProperties
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度器配置属性
 * @date 2025/9/13 20:30
 */
@ConfigurationProperties(prefix = "jy.wrench.config.task.job", ignoreInvalidFields = true)
@Data
public class TaskJobAutoProperties
{
	
	/**
	 * 是否启用任务调度器
	 */
	private boolean enabled = true;
	
	/**
	 * 线程池大小
	 */
	private int poolSize = 10;
	
	/**
	 * 线程名称前缀
	 */
	private String threadNamePrefix = "jy-task-scheduler-";
	
	/**
	 * 关闭时等待任务完成
	 */
	private boolean waitForTasksToCompleteOnShutdown = true;
	
	/**
	 * 等待终止时间（秒）
	 */
	private int awaitTerminationSeconds = 60;
	
	/**
	 * 任务刷新间隔（毫秒）
	 */
	private long refreshInterval = 60000;
	
	/**
	 * 清理无效任务的cron表达式
	 */
	private String cleanInvalidTasksCron = "0 0/10 * * * ?";
}