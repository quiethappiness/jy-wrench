package io.github.quiethappiness.wrencher.trigger;

import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.data.WhiteListDataProvider;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserIdDataProvider implements WhiteListDataProvider
{
	@Override
	public WhiteListType getType()
	{
		return WhiteListType.USER_ID;
	}
	
	@Override
	public List<WhiteListProperties.Rule> getWhitelistData()
	{
		ArrayList<WhiteListProperties.Rule> rules = new ArrayList<>();
		WhiteListProperties.Rule rule = new WhiteListProperties.Rule();
		rule.setUri("/api/v1/index/whitelist");
		rule.setLimit(100);
		rule.setTimeUnit(TimeUnit.SECONDS);
		rule.setWhiteList(new HashMap<>());
		ArrayList<String> strings = new ArrayList<>();
		strings.add("admin123");
		rule.getWhiteList().put(WhiteListType.USER_ID, strings);
		rules.add(rule);
		return rules;
	}
}
