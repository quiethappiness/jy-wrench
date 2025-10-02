package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhiteListResultEntity
{
	private AbstractWhiteListSupport.InWhitListResult inWhitListResult;
}
