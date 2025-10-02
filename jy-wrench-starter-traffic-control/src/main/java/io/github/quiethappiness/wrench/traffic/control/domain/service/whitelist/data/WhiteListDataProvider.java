package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.data;

import io.github.quiethappiness.wrench.traffic.control.config.TrafficControlProperties;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;

import java.util.List;

public interface WhiteListDataProvider
{
	WhiteListType getType();
	
	/**
	 * 获取需要加载到白名单BitSet中的全部数据
	 * @return 返回一个包含所有白名单ID的集合
	 */
	List<TrafficControlProperties.Rule> getWhitelistData();
}
