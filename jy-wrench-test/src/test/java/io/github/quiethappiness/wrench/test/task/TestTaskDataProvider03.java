package io.github.quiethappiness.wrench.test.task;

import io.github.quiethappiness.wrench.task.job.domain.model.TaskJobScheduleVO;
import io.github.quiethappiness.wrench.task.job.provider.ITaskDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 测试任务数据提供者
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
@Service
public class TestTaskDataProvider03 implements ITaskDataProvider
{

    private static final Logger log = LoggerFactory.getLogger(TestTaskDataProvider03.class);

    @Override
    public List<TaskJobScheduleVO> queryAllValidTaskSchedule() {
        List<TaskJobScheduleVO> tasks = new ArrayList<>();

        // 使用简单Runnable的示例
        TaskJobScheduleVO task3 = new TaskJobScheduleVO();
        task3.setId(4L);
        task3.setDescription("测试任务3 - 清理任务");
        task3.setCronExpression("0/15 * * * * ?"); // 每天凌晨2点执行
        task3.setTaskParam("{\"cleanup_days\":7}");
        
        // 使用Runnable方式设置任务逻辑
        Runnable task3Logic = () -> {
            log.info("执行清理任务 - 任务ID: 3");
            // 模拟清理操作
	        try
	        {
		        Thread.sleep(1000); // 模拟耗时操作
	        }
	        catch (InterruptedException e)
	        {
		        throw new RuntimeException(e);
	        }
	        log.info("清理任务执行完成");
        };

        task3.setTaskLogic(task3Logic);
        tasks.add(task3);
        
        return tasks;
    }

    @Override
    public List<Long> queryAllInvalidTaskScheduleIds() {
        // 返回一些无效的任务ID用于测试
        return Arrays.asList(999L, 1000L);
    }

}