package io.github.quiethappiness.wrench.traffic.control.types.enumvo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WhiteListType
{
	USER_ID("userId", "UserId", String.class,"userIdWhiteListDataProvider"),
	IP("ip", "IP", String.class,"ipWhiteListDataProvider"),
	PHONE("phone", "Phone", String.class,"phoneWhiteListDataProvider"),
	// EMAIL("email", "Email"),
	// HEADER("header", "Header"),
	// COOKIE("cookie", "Cookie"),
	// ALL("all", "All"),
	;
	private final String code;
	private final String desc;
	private final Class<?> clazz;
	private final String dataProviderName;
}
