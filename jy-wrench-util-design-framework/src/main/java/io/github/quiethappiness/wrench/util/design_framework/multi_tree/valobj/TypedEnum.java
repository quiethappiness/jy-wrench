package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

/**
 * TypedEnum
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description
 * @date 2025/10/23 11:06
 */
public interface TypedEnum
{
	
	default String getType()
	{
		return this.getClass()
			.getSimpleName();
	}
	
	String getValue();
	
	default boolean equals(TypedEnum other)
	{
		return this.getValue()
			.equals(other.getValue());
	}
}
