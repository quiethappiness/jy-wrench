package io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj;

/**
 * AttributeVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 变量属性
 * @date 2025/9/10 16:35
 */
public class AttributeVO
{
	private String name;
	private String value;
	
	public AttributeVO()
	{
	}
	
	public AttributeVO(String name, String attributeValue)
	{
		this.name = name;
		this.value = attributeValue;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
}