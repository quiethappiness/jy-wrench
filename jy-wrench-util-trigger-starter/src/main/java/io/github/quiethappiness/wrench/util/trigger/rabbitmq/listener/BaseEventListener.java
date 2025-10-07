package io.github.quiethappiness.wrench.util.trigger.rabbitmq.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 通用消息监听器基类
 * 提供通用的消息处理逻辑和异常处理机制
 *
 * @author quietHappiness
 * @version 1.0
 * @date 2025/9/22
 */
@Slf4j
public abstract class BaseEventListener<T>
{
	private final Class<T> messageType;
	
	/**
	 * 构造函数，通过反射自动获取泛型类型
	 */
	@SuppressWarnings("unchecked")
	protected BaseEventListener()
	{
		Type genericSuperclass = getClass().getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length > 0)
			{
				this.messageType = (Class<T>) actualTypeArguments[0];
			}
			else
			{
				throw new IllegalStateException("无法确定泛型参数类型");
			}
		}
		else
		{
			throw new IllegalStateException("无法确定泛型参数类型");
		}
	}
	
	/**
	 * 处理消息
	 *
	 * @param message
	 * 	消息内容
	 */
	public void handleMessage(String message)
	{
		try
		{
			log.info("接收到MQ消息 message:{}", message);
			// 反序列化消息
			T messageObject = JSON.parseObject(message, messageType);
			// 处理业务逻辑
			processMessage(messageObject);
			log.info("MQ消息处理成功 message:{}", message);
		}
		catch (Exception e)
		{
			log.error("MQ消息处理失败 message:{}", message, e);
			handleException(message, e);
		}
	}
	
	/**
	 * 处理消息（带Message对象）
	 *
	 * @param message
	 * 	消息对象
	 */
	public void handleMessage(Message message)
	{
		try
		{
			String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
			log.info("接收到MQ消息 messageBody:{}", messageBody);
			// 反序列化消息
			T messageObject = JSON.parseObject(messageBody, messageType);
			// 处理业务逻辑
			processMessage(messageObject);
			log.info("MQ消息处理成功 messageBody:{}", messageBody);
		}
		catch (Exception e)
		{
			String messageBody = "";
			try
			{
				messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
			}
			catch (Exception ex)
			{
				log.error("无法解析消息体", ex);
			}
			log.error("MQ消息处理失败 messageBody:{}", messageBody, e);
			handleException(messageBody, e);
		}
	}
	
	/**
	 * 处理业务逻辑 - 子类需要实现
	 *
	 * @param message
	 * 	消息对象
	 */
	protected abstract void processMessage(T message) throws Exception;
	
	/**
	 * 异常处理 - 子类可以重写
	 *
	 * @param message
	 * 	消息内容
	 * @param e
	 * 	异常对象
	 */
	protected void handleException(String message, Exception e)
	{
		// 默认实现：记录日志并抛出异常，让RabbitMQ重新投递消息
		throw new RuntimeException("消息处理失败: " + e.getMessage(), e);
	}
}