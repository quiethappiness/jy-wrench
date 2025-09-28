package io.github.quiethappiness.infrastructure.util.rabbitmq.publisher;

public interface IMessage
{
	String uuid();
	String exchange();
	String routingKey();
	String body();
}
