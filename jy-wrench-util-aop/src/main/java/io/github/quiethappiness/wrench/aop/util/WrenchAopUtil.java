package io.github.quiethappiness.wrench.aop.util;

import io.github.quiethappiness.wrench.util.types.common.util.TypeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public record WrenchAopUtil()
{
	static Logger log = LoggerFactory.getLogger(WrenchAopUtil.class);
	
	public interface MethodPart
	{
		
		/**
		 * 执行降级方法并返回结果
		 * @param jp
		 * 	连接点对象，包含目标方法的签名和执行信息
		 * @param fallbackMethod
		 * 	降级方法名称
		 * @return 降级方法执行后的返回结果
		 * @throws Exception
		 * 	当方法调用过程中发生异常时抛出
		 */
		static Object execFallbackMethodAndReturn(JoinPoint jp, String fallbackMethod) throws Exception
		{
			if (!StringUtils.hasText(fallbackMethod))
			{
				log.error("fallbackMethod is null or empty, please check the configuration");
				return new Object();
			}
			Method method;
			try
			{
				method = getFallBackMethodFromJP(jp, fallbackMethod);
				// invoke() 方法用于执行指定对象上的方法
				return method.invoke(jp.getThis(), jp.getArgs());
			}
			catch (NoSuchMethodException e)
			{
				log.error("fallbackMethod is not found, please check the configuration, exception:",e);
				return new Object();
			}
		}
		
		static Method getFallBackMethodFromJP(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException
		{
			// 获取目标方法的签名信息
			// 获取被拦截方法的签名信息
			// Signature 是 AspectJ 中的一个接口，表示程序中各种元素（如方法、构造函数、字段等）的签名
			// 它包含了被拦截元素的基本信息，如名称、声明类型等
			// 可以确保找到的方法签名与原始方法一致，从而正确地调用降级处理方法
			Signature sig = jp.getSignature();
			// 将通用的 Signature 对象转换为 MethodSignature 类型
			// MethodSignature 是 AspectJ 提供的一个接口，专门用于表示方法级别的签名信息
			// 通过转换为 MethodSignature，可以获得更详细的方法信息，如：
			// - 方法参数类型列表
			// - 返回值类型
			// - 方法名称等
			MethodSignature methodSignature = (MethodSignature) sig;
			// 通过反射获取降级方法并执行
			return jp
				.getTarget()
				.getClass()
				.getMethod(fallbackMethod, methodSignature.getParameterTypes());
		}
		
		static Class<?> getTargetClassFromJP(JoinPoint jp)
		{
			return jp
				.getTarget()
				.getClass();
		}
		
		static Method getTargetMethodFromJP(JoinPoint jp) throws NoSuchMethodException
		{
			// 获取目标方法的签名信息
			// 获取被拦截方法的签名信息
			// Signature 是 AspectJ 中的一个接口，表示程序中各种元素（如方法、构造函数、字段等）的签名
			// 它包含了被拦截元素的基本信息，如名称、声明类型等
			// 可以确保找到的方法签名与原始方法一致，从而正确地调用降级处理方法
			Signature sig = jp.getSignature();
			// 将通用的 Signature 对象转换为 MethodSignature 类型
			// MethodSignature 是 AspectJ 提供的一个接口，专门用于表示方法级别的签名信息
			// 通过转换为 MethodSignature，可以获得更详细的方法信息，如：
			// - 方法参数类型列表
			// - 返回值类型
			// - 方法名称等
			MethodSignature methodSignature = (MethodSignature) sig;
			// 通过反射获取降级方法并执行
			return jp
				.getTarget()
				.getClass()
				.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
		}
	}
	public interface FieldPart
	{
		static <T> T extractSpelExpressionValue(ProceedingJoinPoint jp, String expression, Class<T> clazz)
		{
			EvaluationContext context = buildEvaluationContext(jp);
			return extractSpelExpressionValue(context, expression, clazz);
		}
		
		static <T> T extractSpelExpressionValue(EvaluationContext context, String expression, Class<T> clazz)
		{
			ExpressionParser parser = new SpelExpressionParser();
			T value = null;
			// Validate the expression  before parsing
			if (!StringUtils.hasText(expression))
			{
				log.warn("Expression expression is null or empty, using default value");
			}
			else
			{
				try
				{
					// 处理 #{variable} 格式的表达式，将其转换为 #variable 格式
					if (expression.startsWith("#{") && expression.endsWith("}"))
					{
						expression = "#" + expression.substring(2, expression.length() - 1);
					}
					// 解析表达式获取用户ID
					value = parser
						.parseExpression(expression)
						.getValue(context, clazz);
				}
				catch (Exception e)
				{
					log.error("Failed to parse expression: {}, using default value", expression, e);
				}
			}
			return value;
		}
		

		
		static String getFelidStringFromArgOrField(String attrName, ProceedingJoinPoint jp) throws NoSuchMethodException
		{
			Method method = MethodPart.getTargetMethodFromJP(jp);
			return getFelidStringFromArgOrField(attrName, method.getParameters(), jp.getArgs());
		}
		
		/**
		 * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
		 */
		static String getFelidStringFromArgOrField(String attrName, Parameter[] parameters, Object[] args)
		{
			String filedValue = null;
			// 在多个参数中查找包含目标属性的对象
			for (int i = 0; i < parameters.length; i++)
			{
				if (StringUtils.hasText(filedValue))
				{
					break;
				}
				if (parameters[i]
					.getName()
					.equals(attrName) && TypeUtils.isTargetType(args[i].getClass()))
				{
					filedValue = String.valueOf(args[i]);
				}
				else
				// filedValue = BeanUtils.getProperty(arg, attrName);
				// fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
				{
					filedValue = String.valueOf(getCompositionFieldObjectByFieldName(args[i], attrName));
				}
			}
			return filedValue;
		}
		
		/**
		 * 获取对象的特定属性值
		 * <p>不包含自身是基本数据类型的情况</p>
		 * @param bean
		 * 	对象
		 * @param name
		 * 	属性名
		 * @return 属性值
		 * @author tang
		 */
		static Object getCompositionFieldObjectByFieldName(Object bean, String name)
		{
			try
			{
				if (bean == null)
				{
					return null;
				}
				Field field = getCompositionAndSuperFieldByFieldName(bean, name);
				if (field == null)
				{
					return null;
				}
				field.setAccessible(true);
				Object o = field.get(bean);
				field.setAccessible(false);
				return o;
			}
			catch (IllegalAccessException e)
			{
				return null;
			}
		}
		
		/**
		 * 获取字段值（支持嵌套对象）
		 * <pre>
		 *     user.name.lastName
		 * </pre>
		 * <p>路径根据separator分隔符</p>
		 * <p>包含自身</p>
		 * <p>支持嵌套对象</p>
		 * <p>如果参数是target{@link io.github.quiethappiness.wrench.util.types.common.util.TypeUtils}类型，则返回自身</p>
		 */
		static Object getCompositionFieldObjectByFieldPath(String parameterName, Object parameterValue, String fieldPath, String separator) throws Exception
		{
			// 是原生类型
			if (parameterName.equals(fieldPath) && TypeUtils.isTargetType(parameterValue.getClass()))
			{
				return parameterValue;
			}
			String[] fieldNames = fieldPath.split(separator);
			Object current = parameterValue;
			for (String fieldName : fieldNames)
			{
				if (current == null)
				{
					return null;
				}
				Field field = getCompositionAndInheritanceFieldByFieldNameRecursive(current.getClass(), fieldName);
				if (field == null)
				{
					throw new NoSuchFieldException("字段不存在: " + fieldName);
				}
				field.setAccessible(true);
				current = field.get(current);
			}
			return current;
		}
		
		/**
		 * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
		 * <p>不包含自身</p>
		 * @param bean
		 * 	对象
		 * @param name
		 * 	属性名
		 * @return 该属性对应方法
		 * @author tang
		 */
		static Field getCompositionAndSuperFieldByFieldName(Object bean, String name)
		{
			// 是原生类型
			if (TypeUtils.isTargetType(bean.getClass()))
			{
				throw new IllegalArgumentException("参数必须是对象");
			}
			try
			{
				Field field;
				try
				{
					field = bean
						.getClass()
						.getDeclaredField(name);
				}
				catch (NoSuchFieldException e)
				{
					field = bean
						.getClass()
						.getSuperclass()
						.getDeclaredField(name);
				}
				return field;
			}
			catch (NoSuchFieldException e)
			{
				return null;
			}
		}
		
		/**
		 * 递归地在类及其父类中查找指定名称的字段
		 *<p>不包含自身</p>
		 * @param clazz 要查找的类
		 * @param fieldName 字段名称
		 * @return 找到的字段对象，如果未找到则返回null
		 */
		static Field getCompositionAndInheritanceFieldByFieldNameRecursive(Class<?> clazz, String fieldName)
		{
			// 是原生类型
			if (TypeUtils.isTargetType(clazz))
			{
				throw new IllegalArgumentException("参数必须是对象");
			}
			try
			{
				return clazz.getDeclaredField(fieldName);
			}
			catch (NoSuchFieldException e)
			{
				// 尝试从父类查找
				Class<?> superClass = clazz.getSuperclass();
				if (superClass != null && superClass != Object.class)
				{
					return getCompositionAndInheritanceFieldByFieldNameRecursive(superClass, fieldName);
				}
				return null;
			}
		}
		
		static EvaluationContext buildEvaluationContext(String paramName, Object arg)
		{
			// 创建表达式计算上下文，并将方法参数设置为变量
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable(paramName, arg);
			return context;
		}
		
		static EvaluationContext buildEvaluationContext(ProceedingJoinPoint jp)
		{
			MethodSignature signature = (MethodSignature) jp.getSignature();
			String[] paramNames = signature.getParameterNames();
			Object[] args = jp.getArgs();
			// 创建表达式计算上下文，并将方法参数设置为变量
			EvaluationContext context = new StandardEvaluationContext();
			if (paramNames != null)
			{
				for (int i = 0; i < paramNames.length; i++)
				{
					context.setVariable(paramNames[i], args[i]);
				}
			}
			return context;
		}
		
		static EvaluationContext buildEvaluationContext(Method method, Object[] args)
		{
			StandardEvaluationContext evalContext = new StandardEvaluationContext();
			// 设置方法参数
			Parameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++)
			{
				String paramName = parameters[i].getName();
				evalContext
					.setVariable(paramName, args[i]);
				// 同时设置简化的变量名（去掉Request后缀等）
				String simplifiedName = simplifyParameterName(parameters[i]);
				evalContext
					.setVariable(simplifiedName, args[i]);
			}
			// 设置根对象（第一个参数）
			if (args.length > 0 && args[0] != null)
			{
				evalContext
					.setRootObject(args[0]);
			}
			return evalContext;
		}
	}
	static String simplifyParameterName(Parameter parameter)
	{
		String name = parameter
			.getType()
			.getSimpleName();
		if (name.endsWith("Request") || name.endsWith("DTO"))
		{
			name = name.substring(0, name.length() - 7); // 去掉"Request"后缀
		}
		return name
			.substring(0, 1)
			.toLowerCase() + name.substring(1);
	}
	
	/**
	 * 判断是否为配置类（包含@Configuration注解）
	 */
	public static boolean isConfigurationClass(AnnotatedTypeMetadata metadata)
	{
		return metadata.isAnnotated("org.springframework.boot.autoconfigure.SpringBootApplication")
			|| metadata.isAnnotated("org.springframework.boot.autoconfigure.EnableAutoConfiguration")
			|| metadata.isAnnotated("org.springframework.context.annotation.Configuration");
	}
	
	/**
	 * 在所有已注册的Bean中检查是否存在指定注解
	 */
	public static boolean isAnnotationPresentOnAnyBean(ConditionContext context, Class<? extends Annotation> annotationClass)
	{
		try
		{
			// 获取所有已注册的Bean定义名称
			String[] beanNames = context
				.getRegistry()
				.getBeanDefinitionNames();
			for (String beanName : beanNames)
			{
				BeanDefinition beanDef = context
					.getRegistry()
					.getBeanDefinition(beanName);
				if (beanDef instanceof AnnotatedBeanDefinition annotatedDef)
				{
					AnnotationMetadata annMetadata = annotatedDef.getMetadata();
					// 检查是否为配置类且带有指定注解
					if (isConfigurationClass(annMetadata) && annMetadata.isAnnotated(annotationClass.getName()))
					{
						return true;
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("检查Bean注解时发生异常: {}", e.getMessage());
		}
		return false;
	}
}