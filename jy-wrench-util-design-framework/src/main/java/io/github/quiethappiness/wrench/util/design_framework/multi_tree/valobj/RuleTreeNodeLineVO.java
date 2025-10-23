package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * RuleTreeNodeLineVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 规则树节点指向线对象。用于衔接 from->to 节点链路关系.T代表上一个节点的输入参数，D代表限定值类型
 * 	一般同时使用T,D代表对于流程异常清晰。一般推荐只使用一个参数，T，至于D，此时为string
 * @date 2025/10/21 10:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RuleTreeNodeLineVO<T extends IRuleEnumRegistry.TypedEnum, D>
{
	
	/**
	 * 规则树ID
	 */
	private String treeId;
	/**
	 * 规则Key节点 From
	 */
	private String ruleNodeFrom;
	/**
	 * 规则Key节点 To
	 */
	private String ruleNodeTo;
	
	/**
	 * 类型映射，一般是某个枚举
	 */
	private String ruleLimitMapType;
	/* 限定类型；1:=;2:>;3:<;4:>=;5<=;6:enum[枚举范围] */
	// private RuleLimitTypeVO ruleLimitType;
	/**
	 * 限定值（到下个节点）
	 */
	private D ruleLimitValue;
	
	/**
	 * 两种方式，1.从外部赋值，2，挑选好合适的赋予
	 * T,D存在相同的时候，T常表示上一个节点的返回值类型，D指的是用于判断的值类型（一般是枚举），如果T,D都是枚举，那自然最好
	 */
	private BiPredicate<T, D> outerPredicate;
	
	private Predicate<T> innerPredicate;
	
	/**
	 * 一般来讲，这个函数一般不使用，而是使用setter的函数。但是在数据这种自动组建时，可以考虑使用该函数;
	 * 需要先给ruleLimitMapType ，ruleLimitValue赋值
	 * @param ruleEnumRegistry
	 */
	public void setInnerPredicate(IRuleEnumRegistry ruleEnumRegistry)
	{
		if (ruleLimitMapType == null)
		{
			throw new RuntimeException("ruleLimitMapType is null");
		}
		// log.info("ruleLimitMapType:{}", ruleLimitMapType);
		// log.info("ruleLimitValue:{},class:{}", ruleLimitValue, ruleLimitValue.getClass());
		if (ruleLimitValue instanceof String enumInstance)
		{
			IRuleEnumRegistry.TypedEnum localMapEnumInstance = ruleEnumRegistry.getEnum(ruleLimitMapType, enumInstance);
			innerPredicate = localMapEnumInstance::equals;
		}
		else
		{
			throw new RuntimeException("这里使用InnerPredicate似乎并不正确");
		}
	}
	
	/**
	 * 获取类型安全的决策函数
	 */
	public SafeDecisionFunction getSafeDecisionFunction()
	{
		return (matterValue, ruleLimitValue) ->
		{
			try
			{
				@SuppressWarnings("unchecked")
				T typedMatterValue = (T) matterValue;
				@SuppressWarnings("unchecked")
				D typedRuleLimitValue = (D) ruleLimitValue;
				if (outerPredicate != null)
				{
					return outerPredicate.test(typedMatterValue, typedRuleLimitValue);
				}
				if (innerPredicate != null)
				{
					return innerPredicate.test(typedMatterValue);
				}
				throw new RuntimeException("请先设置predicate");
			}
			catch (ClassCastException e)
			{
				return false;
			}
		};
	}
	
	/**
	 * SafeDecisionFunction
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description
	 * @date 2025/10/22 10:00
	 */
	@FunctionalInterface
	public interface SafeDecisionFunction
	{
		boolean test(Object matterValue, Object ruleLimitValue);
	}
}