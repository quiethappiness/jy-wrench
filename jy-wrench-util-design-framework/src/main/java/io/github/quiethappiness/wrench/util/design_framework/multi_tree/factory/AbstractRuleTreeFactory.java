package io.github.quiethappiness.wrench.util.design_framework.multi_tree.factory;

import io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj.RuleTreeR;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractRuleTreeFactory
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树工厂
 * @date 2025/10/21 10:25
 */

@Slf4j
public abstract class AbstractRuleTreeFactory<PT,PR>
{
	public abstract IDecisionTreeEngine<PT,PR> openLogicTree(RuleTreeR.RuleTreeVO<?> ruleTreeVO);
}