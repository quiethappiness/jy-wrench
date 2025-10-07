package io.github.quiethappiness.wrench.util.infrastructure.gateway;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.annotation.Resource;

/**
 * CommonRequestService
 * @author quietHappiness
 * @version 1.0
 * @description 封装http接口-外部调用
 * @date 2025/8/5 20:16
 */

@Slf4j
public class CommonRequestService
{
	public static final String APPLICATION_JSON = "application/json";
	public static final String CONTENT_TYPE = "content-type";
	@Resource
	private OkHttpClient okHttpClient;
	
	public String post(String apiUrl, String notifyRequestDTOJSON) throws Exception
	{
		try
		{
			// 1. 构建参数
			MediaType mediaType = MediaType.parse(APPLICATION_JSON);
			RequestBody body = RequestBody.create(mediaType, notifyRequestDTOJSON);
			Request request = new Request.Builder()
				.url(apiUrl)
				.post(body)
				.addHeader(CONTENT_TYPE, APPLICATION_JSON)
				.build();
			// 2. 调用接口
			try ( Response response = okHttpClient.newCall(request)
				.execute() )
			{
				// 3. 返回结果
				return response.body()
					.string();
			}
		}
		catch (Exception e)
		{
			log.error(" HTTP 接口服务异常 {}", apiUrl, e);
			throw new RuntimeException("HTTP 接口服务异常");
		}
	}
}