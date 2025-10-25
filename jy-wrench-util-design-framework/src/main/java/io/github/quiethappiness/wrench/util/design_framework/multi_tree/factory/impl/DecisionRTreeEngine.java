package io.github.quiethappiness.wrench.util.design_framework.multi_tree.factory.impl;

import io.github.quiethappiness.wrench.util.design_framework.multi_tree.factory.IDecisionTreeEngine;
import io.github.quiethappiness.wrench.util.design_framework.multi_tree.node.ILogicTreeNode;
import io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj.RuleTreeR;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author quietHappiness @jingyue
 * @description 决策树引擎
 * @create 2025/10/21 10:25
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DecisionRTreeEngine<PT, PR> implements IDecisionTreeEngine<PT, PR>
{
	private Map<String, ? extends ILogicTreeNode<PT, PR>> logicTreeNodeGroup;
	private RuleTreeR.RuleTreeVO<?> ruleTreeVO;
	
	@Override
	public PR process(PT data)
	{
		PR returnData = null;
		// 获取基础信息
		String nextNode = ruleTreeVO.getTreeRootRuleNode();
		Map<String, ? extends RuleTreeR.RuleTreeNodeVO<?>> treeNodeMap = ruleTreeVO.getTreeNodeMap();
		// 获取起始节点「根节点记录了第一个要执行的规则」
		RuleTreeR.RuleTreeNodeVO<?> ruleTreeNode;
		while (null != nextNode)
		{
			// 获取节点
			ruleTreeNode = treeNodeMap.get(nextNode);
			// 获取节点对应的逻辑实现
			ILogicTreeNode<PT, PR> logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
			// 执行节点计算
			ILogicTreeNode.TreeActionEntity<?, PR> actionEntity = logicTreeNode.logic(data);
			// 获取“下一步往哪走”
			Object actionGoValue = actionEntity.getActionGoValue();
			// 获取“暂时下一步传递的数据”
			returnData = actionEntity.getPassData();
			log.info("决策树引擎【{}】treeId:{} node:{} result:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, actionGoValue);
			// 获取下个节点的名称
			nextNode = nextNode(actionGoValue, ruleTreeNode.getTreeNodeLineVOList());
		}
		// 返回最终结果
		return returnData;
	}
	
	public String nextNode(Object matterValue, List<? extends RuleTreeR.RuleTreeNodeLineVO<?>> treeNodeLineVOList)
	{
		if (null == treeNodeLineVOList || treeNodeLineVOList.isEmpty())
		{
			return null;
		}
		for (RuleTreeR.RuleTreeNodeLineVO<?> nodeLine : treeNodeLineVOList)
		{
			if (decisionLogic(matterValue, nodeLine))
			{
				return nodeLine.getTo();
			}
		}
		throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
	}
	
	public boolean decisionLogic(Object matterValue, RuleTreeR.RuleTreeNodeLineVO<?> nodeLine)
	{
		if (nodeLine == null)
		{
			return false;
		}
		RuleTreeR.RuleTreeNodeLineVO.SafeDecisionFunction decisionFunction = nodeLine.getSafeDecisionFunction();
		Object ruleLimitValue = nodeLine.getEnumInstanceNameOrLimitValue();
		return decisionFunction.test(matterValue, ruleLimitValue);
	}
}