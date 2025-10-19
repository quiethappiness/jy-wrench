package io.github.quiethappiness.wrench.util.mq.listener.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Slf4j
@EnableRabbit
public class RabbitMQConfig
{
	@Resource
	private ApplicationContext applicationContext;
	
	// @Bean
	// public MessageConverter jackson2JsonMessageConverter(ObjectMapper mapper)
	// {
	// 	mapper.registerModule(new JavaTimeModule());
	// 	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	// 	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	// 	return new Jackson2JsonMessageConverter(mapper);
	// }
	// ========== 监听器容器工厂（关键！）==========
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
		ConnectionFactory connectionFactory
		// , MessageConverter jackson2JsonMessageConverter
	)
	{
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		// factory.setMessageConverter(jackson2JsonMessageConverter); // 统一序列化
		factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
		factory.setPrefetchCount(1);
		// 重试配置（可选）
		return factory;
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(
		RabbitTemplateConfigurer configurer,
		ConnectionFactory connectionFactory
		// , MessageConverter jackson2JsonMessageConverter
	)
	{
		RabbitTemplate template = new RabbitTemplate();
		configurer.configure(template, connectionFactory);
		template.setMandatory(true); // 必须开启才能触发 return
		// template.setMessageConverter(jackson2JsonMessageConverter); // 建议统一序列化
		template.setReturnsCallback(
			(returnedMessage) -> applicationContext.publishEvent(
				new ReturnedMessageEvent(template, returnedMessage))
		);
		return template;
	}
	
	@Bean
	public DirectExchange errorMessageExchange()
	{
		return new DirectExchange("error.direct");
	}
	
	@Bean
	public Queue errorQueue()
	{
		return new Queue("error.queue", true);
	}
	
	@Bean
	public Binding errorBinding(Queue errorQueue, DirectExchange errorMessageExchange)
	{
		return BindingBuilder.bind(errorQueue)
			.to(errorMessageExchange)
			.with("error");
	}
	
	@Bean
	public MessageRecoverer republishMessageRecoverer(
		RabbitTemplate rabbitTemplate)
	{
		return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
	}
	
	@Getter
	@Setter
	@ToString
	@EqualsAndHashCode(callSuper = false)
	public static class ReturnedMessageEvent extends ApplicationEvent
	{
		private ReturnedMessage message;
		
		public ReturnedMessageEvent(Object source, ReturnedMessage message)
		{
			super(source);
			this.message = message;
		}
		
		public ReturnedMessageEvent(Object source)
		{
			super(source);
		}
	}
}
