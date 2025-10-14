package io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AttributeVO
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 变量属性
 * @date 2025/9/10 16:35
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeVO
{
	private String name;
	private String value;
}