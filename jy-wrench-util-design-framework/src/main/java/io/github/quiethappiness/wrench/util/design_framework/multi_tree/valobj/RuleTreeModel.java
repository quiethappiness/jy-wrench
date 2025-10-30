package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface RuleTreeModel
{
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	abstract class RuleTreeNodeLine {
		
		/**
		 * 规则树ID
		 */
		protected String treeId;
		
		/**
		 * 规则Key节点 From
		 */
		protected String ruleNodeFrom;
		
		/**
		 * 规则Key节点 To
		 */
		protected String ruleNodeTo;
		
		/**
		 * 限定值（到下个节点）
		 */
		protected String ruleLimitValue;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	abstract class RuleTreeNode {
		
		/**
		 * 规则树ID
		 */
		protected String treeId;
		
		/**
		 * 规则Key
		 */
		protected String ruleKey;
		
		/**
		 * 规则描述
		 */
		protected String ruleDesc;
		
		/**
		 * 规则比值
		 */
		protected String ruleValue;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	abstract class RuleTree
	{
		
		/**
		 * 规则树ID
		 */
		protected String treeId;
		
		/**
		 * 规则树名称
		 */
		protected String treeName;
		
		/**
		 * 规则树描述
		 */
		protected String treeDesc;
		
		/**
		 * 规则树根入口规则
		 */
		protected String treeNodeRuleKey;
	}
}
