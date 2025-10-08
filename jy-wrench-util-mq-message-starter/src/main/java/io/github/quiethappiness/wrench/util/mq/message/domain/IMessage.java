package io.github.quiethappiness.wrench.util.mq.message.domain;

/**
 * 定义发送消息标准，所有发送消息的类都需要实现此接口
 */
public interface IMessage
{
	String uuid();
	String exchange();
	String routingKey();
	String body();
}