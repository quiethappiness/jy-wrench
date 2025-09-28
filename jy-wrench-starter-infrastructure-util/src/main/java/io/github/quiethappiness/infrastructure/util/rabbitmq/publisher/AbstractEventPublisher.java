package io.github.quiethappiness.infrastructure.util.rabbitmq.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * AbstractEventPublisher
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 将核心代码抽离
 * @date 2025/9/17 18:00
 */
@Slf4j
public abstract class AbstractEventPublisher
{
	protected final String this_exchange;
	protected final RabbitTemplate rabbitTemplate;
	
	protected AbstractEventPublisher(RabbitTemplate rabbitTemplate, String this_exchange)
	{
		this.this_exchange = this_exchange;
		this.rabbitTemplate = rabbitTemplate;
	}
	
	public void publish_Topic_Event(IMessage ms, Consumer<String> func)
	{
		String exchange = null;
		try
		{
			exchange = ms.exchange() != null ? ms.exchange() : this_exchange;
			log.info("发送MQ消息 exchangeName:{},  IMessage:{}", exchange, ms);
			CorrelationData cd = buildCorrelationData(ms.uuid(), func);
			sendWithCol(exchange, ms.routingKey(), ms.body(), cd);
		}
		catch (Exception e)
		{
			log.error("发送MQ消息失败 exchangeName:{}, IMessage:{}", exchange, ms, e);
			throw e;
		}
	}
	
	protected CorrelationData buildCorrelationData(String onlyId, Consumer<String> failedMethod)
	{
		// 1.创建CorrelationData
		CorrelationData cd = new CorrelationData(onlyId);
		// 2.给Future添加ConfirmCallback
		cd.getFuture()
			.addCallback(new ListenableFutureCallback<CorrelationData.Confirm>()
			{
				@Override
				public void onFailure(Throwable ex)
				{
					// 2.1.Future发生异常时的处理逻辑，基本不会触发
					log.error("send message fail", ex);
				}
				
				@Override
				public void onSuccess(CorrelationData.Confirm result)
				{
					// 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
					if (result.isAck())
					{ // result.isAck()，boolean类型，true代表ack回执，false 代表 nack回执
						log.debug("发送消息成功，收到 ack!");
						// 在这里修改订单状态为已发送
					}
					else
					{ // result.getReason()，String类型，返回nack时的异常描述
						// 更新消息状态为失败
						try
						{
							failedMethod.accept(onlyId);
						}
						catch (Exception e)
						{
							log.error("补偿处理失败，原因：", e);
						}
						// return;
						log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
					}
				}
			});
		return cd;
	}
	
	protected void sendWithCol(IMessage ms, CorrelationData cd)
	{
		String exchange = ms.exchange() != null ? ms.exchange() : this_exchange;
		String routingKey = ms.routingKey();
		String message = ms.body();
		sendWithCol(exchange, routingKey, message, cd);
	}
	
	protected void sendWithCol(final String exchange, final String routingKey, final String message, final CorrelationData cd)
	{
		MessagePostProcessor messagePostProcessor = m ->
		{
			// 持久化消息配置
			MessageProperties properties = m.getMessageProperties();
			properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			properties.setMessageId(cd.getId());
			return m;
		};
		if (cd != null)
		{
			rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor, cd);
		}
		else
		{
			rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor);
		}
	}
	
	protected void sendWithCol(String routingKey, String message, CorrelationData cd)
	{
		sendWithCol(this_exchange, routingKey, message, cd);
	}
	
	public void sendWithDelay(String routingKey, Object message, long delay, TimeUnit timeUnit)
	{
		rabbitTemplate.convertAndSend(this_exchange, routingKey, message, msg ->
		{
			msg.getMessageProperties()
				.setDelay((int) timeUnit.toMillis(delay));
			return msg;
		});
	}
}