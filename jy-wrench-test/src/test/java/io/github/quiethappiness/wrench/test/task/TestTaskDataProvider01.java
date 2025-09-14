package io.github.quiethappiness.wrench.test.task;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;
import io.github.quiethappiness.wrench.task.job.provider.ITaskDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 测试任务数据提供者
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
@Service
public class TestTaskDataProvider01 implements ITaskDataProvider
{
	
	private static final Logger log = LoggerFactory.getLogger(TestTaskDataProvider01.class);
	
	@Override
	public List<TaskJobScheduleVO> queryAllValidTaskSchedule()
	{
		List<TaskJobScheduleVO> tasks = new ArrayList<>();
		// 任务1：使用函数式编程方式
		TaskJobScheduleVO task1 = new TaskJobScheduleVO();
		task1.setId(1L);
		task1.setDescription("测试任务1 - 数据处理");
		task1.setCronExpression("0/10 * * * * ?"); // 每10秒执行一次
		task1.setTaskParam("{\"type\":\"data_process\",\"batch_size\":100}");
		// 使用BiConsumer方式设置任务逻辑
		BiConsumer<Long, String> task1Logic = (taskId, taskParam) ->
		{
			log.info("执行测试任务1 - 任务ID: {}, 任务参数: {}", taskId, taskParam);
			try
			{
				Thread.sleep(500); // 模拟任务执行时间
				log.info("测试任务1执行完成");
			}
			catch (InterruptedException e)
			{
				Thread.currentThread()
					.interrupt();
				log.error("测试任务1执行被中断", e);
			}
		};
		task1.setTaskLogic(task1Logic);
		tasks.add(task1);
		return tasks;
	}
	
	@Override
	public List<Long> queryAllInvalidTaskScheduleIds()
	{
		// 返回一些无效的任务ID用于测试
		return Arrays.asList(999L, 1000L);
	}
}