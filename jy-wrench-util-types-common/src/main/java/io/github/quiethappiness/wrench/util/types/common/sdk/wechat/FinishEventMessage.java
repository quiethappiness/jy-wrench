package io.github.quiethappiness.wrench.util.types.common.sdk.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * FinishEventMessage
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 模板消息发送完成
 * @date 2025/8/16 16:41
 */
@XStreamAlias("xml")
@Data
public class FinishEventMessage
{
	@XStreamAlias("ToUserName")
	private String toUserName;
	
	@XStreamAlias("FromUserName")
	private String fromUserName;
	
	@XStreamAlias("CreateTime")
	private String createTime;
	
	@XStreamAlias("MsgType")
	private String msgType;
	
	@XStreamAlias("Event")
	private String event;

	
	@XStreamAlias("MsgID")
	private String msgId;
	
	@XStreamAlias("Status")
	private String status;
}