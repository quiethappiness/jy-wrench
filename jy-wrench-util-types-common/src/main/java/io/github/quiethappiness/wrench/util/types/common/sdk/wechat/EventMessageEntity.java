package io.github.quiethappiness.wrench.util.types.common.sdk.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@XStreamAlias("xml")
@Data
public class EventMessageEntity
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
	
	@XStreamAlias("EventKey")
	private String eventKey;
	
	@XStreamAlias("MsgID")
	private String msgId;
	
	@XStreamAlias("Status")
	private String status;
	
	@XStreamAlias("Ticket")
	private String ticket;
	
	@XStreamAlias("Content")
	private String content;
	
	@XStreamAlias("Latitude")
	private String latitude;
	
	@XStreamAlias("Longitude")
	private String longitude;
	
	@XStreamAlias("Precision")
	private String precision;
	
	@XStreamAlias("Description")
	private String description;
	
	@XStreamAlias("PicUrl")
	private String picUrl;
	
	@XStreamAlias("MediaId")
	private String mediaId;
	
	@XStreamAlias("Format")
	private String format;
	
	@XStreamAlias("ThumbMediaId")
	private String thumbMediaId;
	
	@XStreamAlias("Title")
	private String title;
	
	@XStreamAlias("Url")
	private String url;
	
	@XStreamAlias("ScanType")
	private String scanType;
	
	@XStreamAlias("ScanResult")
	private String scanResult;
	
	@XStreamAlias("SendPicsInfo")
	private String sendPicsInfo;
	
	@XStreamAlias("SendLocationInfo")
	private String sendLocationInfo;
	
	@XStreamAlias("Recognition")
	private String recognition;
	
	@XStreamAlias("CardId")
	private String cardId;
}