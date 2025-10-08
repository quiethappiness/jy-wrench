package io.github.quiethappiness.wrench.task.job.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * TaskJobConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 任务调度器配置
 * @date 2025/9/13 20:30
 */

@AutoConfiguration
@Import(TaskJobConfiguration.class)
public class TaskJobAutoConfiguration
{

}