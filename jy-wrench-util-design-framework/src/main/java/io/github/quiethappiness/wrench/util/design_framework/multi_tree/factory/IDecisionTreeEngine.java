package io.github.quiethappiness.wrench.util.design_framework.multi_tree.factory;

/**
 * IDecisionTreeEngine
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树组合接口
 * @date 2025/10/21 10:26
 */
public interface IDecisionTreeEngine<PT, PR>
{
	PR process(PT data);
}