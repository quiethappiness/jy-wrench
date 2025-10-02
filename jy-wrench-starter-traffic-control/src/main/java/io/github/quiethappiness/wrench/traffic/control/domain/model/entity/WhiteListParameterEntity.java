package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.WhiteListChecker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

@RequiredArgsConstructor
@Getter
public class WhiteListParameterEntity
{
	private final WhiteListChecker whiteListChecker;
	private final ProceedingJoinPoint jp;
	private final WhiteListProperties whiteListProperties;
}
