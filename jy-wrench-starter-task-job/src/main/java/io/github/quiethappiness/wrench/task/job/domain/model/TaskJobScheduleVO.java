package io.github.quiethappiness.wrench.task.job.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * TaskJobScheduleVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度值对象
 * @date 2025/9/13 20:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskJobScheduleVO
{
	/**
	 * 任务ID
	 */
	private Long id;
	
	/**
	 * 任务描述
	 */
	private String description;
	
	/**
	 * Cron表达式
	 */
	private String cronExpression="0 0/10 * * * ?";
	
	/**
	 * 任务参数
	 */
	private String taskParam;
	
	/**
	 * 执行器名称
	 */
	private String executorName;
	
	/**
	 * 任务执行器函数式接口
	 */
	private Supplier<Runnable> taskExecutor;
	
	/**
	 * 便捷方法：设置任务执行逻辑
	 * @param taskLogic
	 * 	任务执行逻辑
	 */
	public void setTaskLogic(Runnable taskLogic)
	{
		this.taskExecutor = () -> taskLogic;
	}
	
	/**
	 * 便捷方法：设置带参数的任务执行逻辑
	 * @param taskLogic
	 * 	任务执行逻辑，接收taskId和taskParam
	 */
	public void setTaskLogic(BiConsumer<Long, String> taskLogic)
	{
		this.taskExecutor = () -> (Runnable) () -> taskLogic.accept(id, taskParam);
	}
}