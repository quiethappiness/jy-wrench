package io.github.quiethappiness.wrench.util.infrastructure.rabbitmq.publisher;

public interface IMessage
{
	String uuid();
	String exchange();
	String routingKey();
	String body();
}
