package io.github.quiethappiness.wrench.util.types.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AppExceptionType
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description app异常类型
 * @date 2025/8/18 23:15
 */
@AllArgsConstructor
@Getter
public enum AppExceptionType
{
	UNKNOWN_ERROR("0000", "未知错误"),
	INVALID_PARAM("0001", "参数错误"),
	INVALID_TOKEN("0002", "无效的token"),
	NO_LOGIN_TOKEN("0033", "未携带loginToken, 禁止访问"),
	LOGIN_TOKEN_EXPIRED("0034", "登录token已过期, 禁止访问"),
	INVALID_LOGIN_TOKEN("0035", "无效的loginToken, 禁止访问"),
	NO_COOKIE("0036", "未携带Cookie, 禁止访问"),
	LOCK_MARKET_PAY_ORDER_ERROR("0037", "锁定营销支付订单异常"),
	SETTLE_MARKET_PAY_ORDER_ERROR("0038", "结算营销支付订单异常"),
	REFUND_MARKET_PAY_ORDER_ERROR("0038", "结算营销支付订单异常"),
	UPDATE_ZERO("0039", "更新记录为0"),
	;
	private String code;
	private String message;
}