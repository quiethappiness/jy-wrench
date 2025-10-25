package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;


public record RuleTreeR()
{
	
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
	public static class RuleTreeVO<D>
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
		private Map<String, RuleTreeNodeVO<D>> treeNodeMap = new ConcurrentHashMap<>();
		
		public void addTreeNodeVO(RuleTreeNodeVO<D> treeNodeVO)
		{
			treeNodeMap.put(treeNodeVO.getRuleKey(), treeNodeVO);
		}
		
		public void addTreeNodeVOs(List<RuleTreeNodeVO<D>> treeNodeVOs)
		{
			treeNodeVOs.forEach(this::addTreeNodeVO);
		}
	}
	
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
	public static class RuleTreeNodeVO<D>
	{
		
		/**
		 * 规则树ID
		 */
		private String treeId;
		/**
		 * 规则Key
		 */
		private String ruleKey;
		/**
		 * 规则描述
		 */
		private String ruleDesc;
		/**
		 * 规则比值
		 */
		private String ruleValue;
		
		/**
		 * 规则连线
		 */
		private List<RuleTreeNodeLineVO<D>> treeNodeLineVOList = new ArrayList<>();
		
		public void addTreeNodeLineVO(RuleTreeNodeLineVO< D> treeNodeLineVO)
		{
			treeNodeLineVOList.add(treeNodeLineVO);
		}
		
		public void addTreeNodeLineVOs(List<RuleTreeNodeLineVO<D>> treeNodeLineVOS)
		{
			treeNodeLineVOList.addAll(treeNodeLineVOS);
		}
	}
	
	/**
	 * RuleTreeNodeLineVO
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description 规则树节点指向线对象。用于衔接 from->to 节点链路关系.T代表上一个节点的输入参数，D代表限定值类型
	 * 	一般同时使用T,D代表对于流程异常清晰。一般推荐只使用一个参数，T，至于D，此时为string
	 * @date 2025/10/21 10:17
	 */
	@Data
	// @AllArgsConstructor
	// @NoArgsConstructor
	@Builder(access = AccessLevel.PRIVATE)
	@Slf4j
	public static class RuleTreeNodeLineVO<D>
	{
		
		/**
		 * 规则树ID
		 */
		private String treeId;
		/**
		 * 规则Key节点 From
		 */
		private String from;
		/**
		 * 规则Key节点 To
		 */
		private String to;
		
		/**
		 * 类型映射，一般是某个枚举
		 */
		private String enumType;
		/* 限定类型；1:=;2:>;3:<;4:>=;5<=;6:enum[枚举范围] */
		// private RuleLimitTypeVO ruleLimitType;
		/**
		 * 限定值（到下个节点）
		 */
		private D enumInstanceNameOrLimitValue;
		// 携带类型令牌
		private transient EnumTypeResolver.TypeToken<?> typeToken;
		/**
		 * 两种方式，1.从外部赋值，2，挑选好合适的赋予
		 * T,D存在相同的时候，T常表示上一个节点的返回值类型，D指的是用于判断的值类型（一般是枚举），如果T,D都是枚举，那自然最好
		 */
		private BiPredicate<Object, D> outerPredicate;
		
		private Predicate<Object> innerPredicate;
		
		/**
		 * 根据类型名称创建泛型实例
		 */
		public static < D> RuleTreeR.RuleTreeNodeLineVO<D> createLine(
			EnumTypeResolver resolver,
			String typeName, String treeId,
			String from, String to, D enumInstanceNameOrLimitValue)
		{
			EnumTypeResolver.TypeToken<?> typeToken = resolver.resolveTypeToken(typeName);
			
			RuleTreeNodeLineVO<D> build = RuleTreeNodeLineVO
				.<D>builder()
				.treeId(treeId)
				.from(from)
				.to(to)
				.enumType(typeName)
				.enumInstanceNameOrLimitValue(enumInstanceNameOrLimitValue)
				.typeToken(typeToken)
				.outerPredicate(null)
				.innerPredicate(null)
				.build();
			build.setInnerPredicate(resolver.getEnumRegistry());
			return build;
		}
		
		public static < D> RuleTreeR.RuleTreeNodeLineVO<D> createLine(
			IRuleEnumRegistry enumRegistry,
			String typeName, String treeId,
			String from, String to, D enumInstanceNameOrLimitValue
		)
		{
			EnumTypeResolver resolver = new EnumTypeResolver(enumRegistry);
			EnumTypeResolver.TypeToken<?> typeToken = resolver.resolveTypeToken(typeName);
			RuleTreeNodeLineVO<D> build = RuleTreeNodeLineVO
				.<D>builder()
				.treeId(treeId)
				.from(from)
				.to(to)
				.enumType(typeName)
				.enumInstanceNameOrLimitValue(enumInstanceNameOrLimitValue)
				.typeToken(typeToken)
				.outerPredicate(null)
				.innerPredicate(null)
				.build();
			build.setInnerPredicate(enumRegistry);
			return build;
		}
		
		public static <T extends Enum<T> & TypedEnum, D> RuleTreeR.RuleTreeNodeLineVO<D> createLineByTypeName(
			EnumTypeResolver.TypeToken<T> typeToken,
			String typeName, String treeId,
			String from, String to, D enumInstanceNameOrLimitValue, BiPredicate<Object, D> outerPredicate)
		{
			return RuleTreeR.RuleTreeNodeLineVO
				.<D>builder()
				.treeId(treeId)
				.from(from)
				.to(to)
				.enumType(typeName)
				.enumInstanceNameOrLimitValue(enumInstanceNameOrLimitValue)
				.typeToken(typeToken)
				.outerPredicate(outerPredicate)
				.build();
		}
		
		/**
		 * 获取枚举类型
		 */
		private Class<?> getEnumType()
		{
			if (typeToken != null)
			{
				return typeToken.getType();
			}
			throw new IllegalStateException("无法确定枚举类型");
		}
		
		/**
		 * 一般来讲，这个函数一般不使用，而是使用setter的函数。但是在数据这种自动组建时，可以考虑使用该函数;
		 * 需要先给ruleLimitMapType ，ruleLimitValue赋值
		 */
		public void setInnerPredicate(IRuleEnumRegistry enumRegistry)
		{
			if (enumType == null)
			{
				throw new IllegalArgumentException("ruleLimitMapType is null");
			}
			Class<?> enumType = getEnumType();
			if (enumInstanceNameOrLimitValue instanceof String stringValue)
			{
				TypedEnum enumInstance = enumRegistry
					.getEnum(this.enumType, stringValue);
				if (enumType.isInstance(enumInstance))
				{
					this.innerPredicate = enumInstance::equals;
				}
				else
				{
					throw new IllegalArgumentException("枚举实例类型不匹配");
				}
			}
			else
			{
				throw new IllegalArgumentException("ruleLimitValue必须是String类型");
			}
		}
		
		/**
		 * 类型安全的决策函数
		 */
		public SafeDecisionFunction getSafeDecisionFunction()
		{
			Class<?> enumType = getEnumType();
			return new TypedSafeDecisionFunction(enumType);
		}
		
		/**
		 * 类型安全的决策函数实现
		 */
		private class TypedSafeDecisionFunction implements SafeDecisionFunction
		{
			private final Class<?> expectedType;
			
			public TypedSafeDecisionFunction(Class<?> expectedType)
			{
				this.expectedType = expectedType;
			}
			
			@Override
			public boolean test(Object matterValue, Object ruleLimitValue)
			{
				try
				{
					// 严格的类型检查
					if (!expectedType.isInstance(matterValue))
					{
						return false;
					}
					Object typedMatterValue = getEnumType().cast(matterValue);
					if (outerPredicate != null)
					{
						// 对ruleLimitValue进行类型检查
						if (ruleLimitValue != null &&
							!isCompatibleType(ruleLimitValue))
						{
							return false;
						}
						@SuppressWarnings("unchecked")
						D typedRuleLimitValue = (D) ruleLimitValue;
						return outerPredicate.test(typedMatterValue, typedRuleLimitValue);
					}
					if (innerPredicate != null)
					{
						return innerPredicate.test(typedMatterValue);
					}
					throw new IllegalStateException("请先设置predicate");
				}
				catch (Exception e)
				{
					log.error("决策函数执行异常", e);
					return false;
				}
			}
			
			private boolean isCompatibleType(Object value)
			{
				// 根据D的实际类型进行检查
				// 这里可以根据需要实现更复杂的类型兼容性检查
				return true; // 简化实现
			}
		}
		
		@FunctionalInterface
		public interface SafeDecisionFunction
		{
			boolean test(Object matterValue, Object ruleLimitValue);
		}
	}
	
	/**
	 * 枚举类型解析器
	 */
	@Data
	public static class EnumTypeResolver
	{
		private final IRuleEnumRegistry enumRegistry;
		private final Map<String, TypeToken<?>> typeTokenCache = new ConcurrentHashMap<>();
		
		public EnumTypeResolver(IRuleEnumRegistry enumRegistry)
		{
			this.enumRegistry = enumRegistry;
		}
		
		/**
		 * 根据类型名称获取TypeToken
		 */
		@SuppressWarnings("unchecked")
		public <T extends Enum<T> & TypedEnum> TypeToken<T> resolveTypeToken(String typeName)
		{
			return (TypeToken<T>) typeTokenCache.computeIfAbsent(typeName, name ->
			{
				// 通过枚举注册表获取枚举实例，然后获取其Class
				TypedEnum sample = enumRegistry.getAnyEnum(name);
				if (sample != null)
				{
					Class<?> enumClass = sample.getClass();
					return new TypeToken<>((Class<T>) enumClass);
				}
				throw new IllegalArgumentException("未知的枚举类型: " + name);
			});
		}
		
		/**
		 * 类型令牌 - 用于在运行时保留泛型信息
		 */
		@Getter
		public static class TypeToken<T>
		{
			private final Class<T> type;
			
			@SuppressWarnings("unchecked")
			public TypeToken()
			{
				this.type = (Class<T>) ((ParameterizedType)
					getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			}
			
			public TypeToken(Class<T> type)
			{
				this.type = type;
			}
		}
	}
}
