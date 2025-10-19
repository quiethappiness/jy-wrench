package io.github.quiethappiness.wrench.util.types.common.sdk.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * LocationEventMessage
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 用户地位位置上报
 * @date 2025/8/16 15:58
 */
@XStreamAlias("xml")
@Data
public class LocationEventMessage
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
	
	@XStreamAlias("Latitude")
	private String latitude;
	
	@XStreamAlias("Longitude")
	private String longitude;
	
	@XStreamAlias("Precision")
	private String precision;
}