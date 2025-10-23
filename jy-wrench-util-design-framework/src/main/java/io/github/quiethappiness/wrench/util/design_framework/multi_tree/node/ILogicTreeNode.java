package io.github.quiethappiness.wrench.util.design_framework.multi_tree.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ILogicTreeNode
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树接口
 * @date 2025/10/21 10:20
 */
public interface ILogicTreeNode<T, R>
{
	TreeActionEntity<?, R> logic(T data);
	
	/**
	 * 决策树个动作实习
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	class TreeActionEntity<T,R> {
		private T actionGoValue;
		private R passData;
	}
}