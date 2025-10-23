package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * RuleTreeNodeVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树节点对象
 * @date 2025/10/21 10:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {
	
	/** 规则树ID */
	private String treeId;
	/** 规则Key */
	private String ruleKey;
	/** 规则描述 */
	private String ruleDesc;
	/** 规则比值 */
	private String ruleValue;
	
	/** 规则连线 */
	private List<RuleTreeNodeLineVO<?,?>> treeNodeLineVOList = new ArrayList<>();
	
	public void addTreeNodeLineVO(RuleTreeNodeLineVO<?,?> treeNodeLineVO)
	{
		treeNodeLineVOList.add(treeNodeLineVO);
	}
	public void addTreeNodeLineVOs(List<RuleTreeNodeLineVO<?,?>> treeNodeLineVOS)
	{
		treeNodeLineVOList.addAll(treeNodeLineVOS);
	}
}