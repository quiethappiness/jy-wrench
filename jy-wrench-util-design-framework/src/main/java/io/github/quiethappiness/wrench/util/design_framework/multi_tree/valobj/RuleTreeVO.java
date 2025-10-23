package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RuleTreeVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树对象【注意；不具有唯一ID，不需要改变数据库结果的对象，可以被定义为值对象】
 * @date 2025/10/21 10:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVO
{
	
	/**
	 * 规则树ID
	 */
	private String treeId;
	/**
	 * 规则树名称
	 */
	private String treeName;
	/**
	 * 规则树描述
	 */
	private String treeDesc;
	/**
	 * 规则根节点
	 */
	private String treeRootRuleNode;
	
	/**
	 * 规则节点
	 */
	private Map<String, RuleTreeNodeVO> treeNodeMap = new ConcurrentHashMap<>();
	
	public void addTreeNodeVO(RuleTreeNodeVO treeNodeVO)
	{
		treeNodeMap.put(treeNodeVO.getRuleKey(), treeNodeVO);
	}
	
	public void addTreeNodeVOs(List<RuleTreeNodeVO> treeNodeVOs)
	{
		treeNodeVOs.forEach(this::addTreeNodeVO);
	}
	
	public interface REDIS_KEY
	{
		String PREFIX = "big_market:" + RuleTreeVO.class.getSimpleName() + "_";
		
		static String spliceEntityKey(String strategyId)
		{
			return PREFIX + "entity_" + strategyId;
		}
		
		static String spliceKeyAll()
		{
			return PREFIX + "all";
		}
	}
}