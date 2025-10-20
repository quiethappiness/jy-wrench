package io.github.quiethappiness.wrench.util.types.common.exception;

import io.github.quiethappiness.wrench.util.types.common.enums.AppExceptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;



@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException
{
	
	private static final long serialVersionUID = 5317680961212299217L;
	
	/**
	 * 异常码
	 */
	private String code;
	
	/**
	 * 异常信息
	 */
	private String info;
	
	public AppException(String code)
	{
		this.code = code;
	}
	
	public AppException(String code, Throwable cause)
	{
		this.code = code;
		super.initCause(cause);
	}
	
	public AppException(String code, String message)
	{
		this.code = code;
		this.info = message;
	}
	
	public AppException(String code, String message, Throwable cause)
	{
		this.code = code;
		this.info = message;
		super.initCause(cause);
	}
	
	public AppException(@NotNull AppExceptionType appExceptionType)
	{
		this.code = appExceptionType.getCode();
		this.info = appExceptionType.getMessage();
	}
	
	@Override
	public String toString()
	{
		return AppException.class.getName() + "{" +
			"code='" + code + '\'' +
			", info='" + info + '\'' +
			'}';
	}
}