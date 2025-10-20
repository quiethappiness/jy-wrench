package io.github.quiethappiness.util.mq.publisher.publisher;

import io.github.quiethappiness.wrench.util.mq.message.domain.IMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * RabbitTopicEventPublisher
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 将核心代码抽离
 * @date 2025/9/17 18:00
 */
@Slf4j
public abstract class RabbitTopicEventPublisher
{
	protected final String this_exchange;
	protected final RabbitTemplate rabbitTemplate;
	
	protected RabbitTopicEventPublisher(RabbitTemplate rabbitTemplate, String this_exchange)
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
	
	protected CorrelationData buildCorrelationData2(String onlyId, Consumer<String> failedMethod)
	{
		CorrelationData cd = new CorrelationData(onlyId);
		cd.getFuture()
			.whenComplete((result, ex) ->
			{
				if (ex != null)
				{
					// 处理异常情况
					log.error("send message fail", ex);
				}
				else if (result.isAck())
				{
					// 成功确认
					log.debug("发送消息成功，收到 ack!");
				}
				else
				{
					// 否定确认
					try
					{
						failedMethod.accept(onlyId);
					}
					catch (Exception e)
					{
						log.error("补偿处理失败，原因：", e);
					}
					log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
				}
			});
		return cd;
	}
	
	protected CorrelationData buildCorrelationData(String onlyId, Consumer<String> failedMethod)
	{
		CorrelationData cd = new CorrelationData(onlyId);
		cd.getFuture()
			.thenApply(confirm ->
			{
				// 这里可以转换结果
				return confirm;
			})
			.thenAccept(confirm ->
			{
				// 处理确认结果
				if (confirm.isAck())
				{
					log.debug("发送消息成功，收到 ack!");
				}
				else
				{
					try
					{
						failedMethod.accept(onlyId);
					}
					catch (Exception e)
					{
						log.error("补偿处理失败，原因：", e);
					}
					log.error("发送消息失败，收到 nack, reason : {}", confirm.getReason());
				}
			})
			.exceptionally(throwable ->
			{
				// 处理异常
				log.error("send message fail", throwable);
				return null;
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
				.setDelayLong(timeUnit.toMillis(delay));
			return msg;
		});
	}
}