package io.github.quiethappiness.wrench.util.domain.rabbitmq.message;

public interface IMessage
{
	String uuid();
	String exchange();
	String routingKey();
	String body();
}