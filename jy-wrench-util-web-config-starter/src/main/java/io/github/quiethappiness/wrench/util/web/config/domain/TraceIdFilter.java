package io.github.quiethappiness.wrench.util.web.config.domain;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Validated
public class TraceIdFilter extends OncePerRequestFilter
{
	
	private static final String TRACE_ID = "trace-id";
	
	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException
	{
		try
		{
			String traceId = UUID.randomUUID()
				.toString();
			MDC.put(TRACE_ID, traceId);
			filterChain.doFilter(request, response);
		}
		finally
		{
			MDC.clear();
		}
	}
}