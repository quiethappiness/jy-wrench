package io.github.quiethappiness.wrench.traffic.control.types.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler
 * @description
 * @author quietHappiness @jingyue
 * @date 2025/11/2 10:31
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
	
	@ExceptionHandler(IdempotentException.class)
	public ResponseEntity<String> handleIdempotentException(IdempotentException e)
	{
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(e.getMessage());
	}
}