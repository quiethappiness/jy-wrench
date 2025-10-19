package io.github.quiethappiness.wrench.util.mq.listener.domain.spring;

import io.github.quiethappiness.wrench.util.mq.listener.config.RabbitMQConfig;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReliableMessageListener
{
	
	// private final ITaskMessageService taskService;
	
	@EventListener
	@Async // 可选：异步处理，避免阻塞 RabbitMQ 线程
	public void handleReturnedMessage(RabbitMQConfig.ReturnedMessageEvent event) throws UnsupportedEncodingException
	{
		ReturnedMessage returnedMessage = event.getMessage();
		// message: 被退回的原始消息
		Message message = returnedMessage.getMessage();
		String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
		// replyCode: 错误码，如 312 (NO_ROUTE)
		int replyCode = returnedMessage.getReplyCode();
		// replyText: 错误信息，如 "NO_ROUTE"
		String replyText = returnedMessage.getReplyText();
		// exchange: 目标 exchange
		String exchange = returnedMessage.getExchange();
		// routingKey: 使用的 routing key
		String routingKey = returnedMessage.getRoutingKey();
		String msgId = message.getMessageProperties()
			.getMessageId();
		log.error(" ❌ 消息 {},内容{}, 未到达 {}，路由键为 {}，错误码为 {}，错误信息为 {}", msgId, messageBody, exchange, routingKey, replyCode, replyText);
		// taskService.dealWithReturnedMessage(msgId, messageBody, exchange, routingKey);
	}
}
