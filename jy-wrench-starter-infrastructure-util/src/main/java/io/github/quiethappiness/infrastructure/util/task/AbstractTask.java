package io.github.quiethappiness.infrastructure.util.task;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * AbstractTask
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description task的抽象实现
 * @date 2025/9/20 23:55
 */
@Slf4j
public abstract class AbstractTask<T extends IJob> implements ITask<T>
{
	
	protected Function<T, TaskJobResult> process;
	protected Function<T, TaskJobResult> handleSuccess;
	protected Function<T, TaskJobResult> handleError;
	protected Function<T, TaskJobResult> handleUnknown = job -> TaskJobResult.RETRY;
	
	@Override
	@Async
	public void execJobAsync(T notifyTaskEntity) throws Exception
	{
		Map<String, Integer> map = execJob(Collections.singletonList(notifyTaskEntity));
		log.info("execTaskJob end, map:{}", JSON.toJSONString(map));
	}
	
	@Override
	public Map<String, Integer> execJob(List<T> list) throws Exception
	{
		if (null == list || list.isEmpty())
		{
			return buildResultMap(0, 0, 0, 0);
		}
		log.info("execTaskJob start, jobEntityList:{}", JSON.toJSONString(list));
		int successCount = 0;
		int errorCount = 0;
		int retryCount = 0;
		for (T job : list)
		{
			log.info("job:{}", JSON.toJSONString(job));
			TaskJobResult result = processJob(job);
			switch (result)
			{
				case SUCCESS:
					successCount += 1;
					break;
				case ERROR:
					errorCount += 1;
					break;
				case RETRY:
					retryCount += 1;
					break;
			}
		}
		return buildResultMap(list.size(), successCount, errorCount, retryCount);
	}
	
	private TaskJobResult processJob(T job) throws Exception
	{
		// 回调处理 success 成功，error 失败
		TaskJobResult response = process.apply(job);
		// 更新状态判断&变更数据库表回调任务状态
		if (TaskJobResult.SUCCESS.equals(response))
		{
			// 结果返回success或者retry
			return handleSuccessResponse(job);
		}
		else if (TaskJobResult.ERROR.equals(response))
		{
			// 结果返回 error 或者retry
			return handleErrorResponse(job);
		}
		else
		{
			// 结果返回 unknown或者retry
			return handleUnknownResponse(job);
		}
	}
	
	private TaskJobResult handleSuccessResponse(T notifyTask)
	{
		return handleSuccess.apply(notifyTask);
	}
	
	private TaskJobResult handleErrorResponse(T notifyTask)
	{
		return handleError.apply(notifyTask);
	}
	private TaskJobResult handleUnknownResponse(T notifyTask)
	{
		return handleUnknown.apply(notifyTask);
	}
	protected Map<String, Integer> buildResultMap(int waitCount, int successCount, int errorCount, int retryCount)
	{
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("waitCount", waitCount);
		resultMap.put("successCount", successCount);
		resultMap.put("errorCount", errorCount);
		resultMap.put("retryCount", retryCount);
		return resultMap;
	}
	
	@AllArgsConstructor
	@Getter
	protected enum TaskJobResult
	{
		SUCCESS("success"),
		ERROR("error"),
		RETRY("retry"),
		UNKNOWN("unknown"),
		;
		private final String code;
		
		public static TaskJobResult getByCode(String code)
		{
			for (TaskJobResult value : values())
			{
				if (value.code.equals(code))
				{
					return value;
				}
			}
			throw new IllegalArgumentException("TaskJobResult code not exist");
		}
	}
}