package io.github.quiethappiness.wrench.util.design_framework.multi_tree.factory;

import io.github.quiethappiness.wrench.util.design_framework.multi_tree.node.IRuleEnumRegistry;
import io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj.RuleTreeModel;
import io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj.RuleTreeR;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AbstractRuleTreeFactory
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树工厂
 * @date 2025/10/21 10:25
 */

@Slf4j
public abstract class AbstractRuleTreeFactory<PT, PR>
{
	public abstract IDecisionTreeEngine<PT, PR> openLogicTree(RuleTreeR.RuleTreeVO<?> ruleTreeVO);
	
	public static RuleTreeR.RuleTreeVO<String> spliceRuleTreeVO(
		IRuleEnumRegistry enumRegistry,
		String typedEnum,
		List<? extends RuleTreeModel.RuleTreeNodeLine> ruleTreeNodeLines,
		List<? extends RuleTreeModel.RuleTreeNode> ruleTreeNodes,
		RuleTreeModel.RuleTree ruleTree)
	{
		// List<RuleTreeModel.RuleTreeNodeLine> ruleTreeNodeLineList = (List<RuleTreeModel.RuleTreeNodeLine>) ruleTreeNodeLines;
		var lineVOMap = ruleTreeNodeLines
			.stream()
			.collect(Collectors.groupingBy(
				RuleTreeModel.RuleTreeNodeLine::getRuleNodeFrom,
				Collectors.mapping(
					ruleTreeNodeLine -> RuleTreeR.RuleTreeNodeLineVO
						.createLine(
							enumRegistry,
							typedEnum,
							ruleTreeNodeLine.getTreeId(),
							ruleTreeNodeLine.getRuleNodeFrom(),
							ruleTreeNodeLine.getRuleNodeTo(),
							ruleTreeNodeLine.getRuleLimitValue()
						), Collectors.toList())));
		var nodeVOMap
			= ruleTreeNodes
			.stream()
			.map(ruleTreeNode -> RuleTreeR.RuleTreeNodeVO
				.<String>builder()
				.treeId(ruleTreeNode.getTreeId())
				.ruleKey(ruleTreeNode.getRuleKey())
				.ruleValue(ruleTreeNode.getRuleValue())
				.ruleDesc(ruleTreeNode.getRuleDesc())
				.treeNodeLineVOList(lineVOMap.get(ruleTreeNode.getRuleKey()))
				.build()
			)
			.collect(Collectors.toMap(RuleTreeR.RuleTreeNodeVO::getRuleKey, Function.identity()));
		return RuleTreeR.RuleTreeVO
			.<String>builder()
			.treeId(ruleTree.getTreeId())
			.treeName(ruleTree.getTreeName())
			.treeDesc(ruleTree.getTreeDesc())
			.treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
			.treeNodeMap(nodeVOMap)
			.build();
	}
}