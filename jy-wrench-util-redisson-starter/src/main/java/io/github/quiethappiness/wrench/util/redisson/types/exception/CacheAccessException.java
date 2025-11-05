package io.github.quiethappiness.wrench.util.redisson.types.exception;

public class CacheAccessException extends RuntimeException
{
	public CacheAccessException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public CacheAccessException(String message)
	{
		super(message);
	}
}