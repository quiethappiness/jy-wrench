package io.github.quiethappiness.wrench.trigger;

import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business.WhiteListDataProvider;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserIdDataProvider implements WhiteListDataProvider
{
	@Override
	public WhiteListType getType()
	{
		return WhiteListType.USER_ID;
	}
	
	@Override
	public Set<String> getWhitelistData()
	{
		HashSet<String> hashSet = new HashSet<>();
		hashSet.add("user1");
		hashSet.add("user2");
		hashSet.add("user3");
		return hashSet;
	}
}
