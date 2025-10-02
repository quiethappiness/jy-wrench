package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

@RequiredArgsConstructor
@Getter
public class WhiteListParameterEntity
{
	private final TcWhiteList tcWhiteList;
	private final ProceedingJoinPoint jp;
	private final WhiteListProperties whiteListProperties;
}
