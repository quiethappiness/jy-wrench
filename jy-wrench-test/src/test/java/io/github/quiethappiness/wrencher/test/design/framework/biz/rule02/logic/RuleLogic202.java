package io.github.quiethappiness.wrencher.test.design.framework.biz.rule02.logic;

import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.handler.IBranchLogicHandler;
import io.github.quiethappiness.wrencher.test.design.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author quiethappiness @jignyue
 * @description
 * @create 2025-01-18 09:18
 */
@Slf4j
@Service
public class RuleLogic202 implements IBranchLogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse>
{

    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception{

        log.info("link model02 RuleLogic202");

        return stop(requestParameter, dynamicContext, new XxxResponse("hi 小傅哥！"));
    }

}