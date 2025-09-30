package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;

import java.util.Set;

public interface WhiteListDataProvider
{
	WhiteListType getType();
	
	/**
	 * 获取需要加载到白名单BitSet中的全部数据
	 * @return 返回一个包含所有白名单ID的集合
	 */
	Set<String> getWhitelistData();
}
