package io.github.quiethappiness.wrench.util.types.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LocalMessageResultEnumVO
 * @author quietHappiness
 * @version 1.0
 * @description 回调任务状态-枚举
 * @date 2025/8/5 21:44
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LocalMessageResultEnumVO
{
	
	SUCCESS("success", "成功"),
	ERROR("error", "失败"),
	NULL(null, "空执行"),
	;
	
	private String code;
	private String info;
	
	public static LocalMessageResultEnumVO getByCode(String code)
	{
		switch (code)
		{
			case "success":
				return SUCCESS;
			case "error":
				return ERROR;
			default:
				return NULL;
		}
	}
}