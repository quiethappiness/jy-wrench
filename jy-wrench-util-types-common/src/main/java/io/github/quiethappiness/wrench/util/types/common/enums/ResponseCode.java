package io.github.quiethappiness.wrench.util.types.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode
{
	SUCCESS("0000", "调用成功"),
	UN_ERROR("0001", "调用失败"),
	ILLEGAL_PARAMETER("0002", "非法参数"),
	NO_LOGIN("0003", "未登录"),
	;
	
	private String code;
	private String info;
}