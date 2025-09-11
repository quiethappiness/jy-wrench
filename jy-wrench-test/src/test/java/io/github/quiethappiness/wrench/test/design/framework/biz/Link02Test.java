package io.github.quiethappiness.wrench.test.design.framework.biz;

import com.alibaba.fastjson.JSON;
import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.chain.BranchLinkedList;
import io.github.quiethappiness.wrench.test.design.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import io.github.quiethappiness.wrench.test.design.framework.biz.rule02.logic.XxxResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Link02Test
{
	
	@Resource(name = "demo01")
	private BranchLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList01;
	
	@Resource(name = "demo02")
	private BranchLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList02;
	
	@Test
	public void test_model02_01() throws Exception
	{
		XxxResponse apply = businessLinkedList01.apply("123", new Rule02TradeRuleFactory.DynamicContext());
		log.info("测试结果:{}", JSON.toJSONString(apply));
	}
	
	@Test
	public void test_model02_02() throws Exception
	{
		XxxResponse apply = businessLinkedList02.apply("123", new Rule02TradeRuleFactory.DynamicContext());
		log.info("测试结果:{}", JSON.toJSONString(apply));
	}
}