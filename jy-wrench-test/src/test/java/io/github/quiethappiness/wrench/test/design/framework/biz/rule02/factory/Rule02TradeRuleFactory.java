package io.github.quiethappiness.wrench.test.design.framework.biz.rule02.factory;

import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.AbstractDynamicContext;
import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.BranchLinkArmory;
import io.github.quiethappiness.wrench.design.framework.link.model2.proceed_check.chain.BranchLinkedList;
import io.github.quiethappiness.wrench.test.design.framework.biz.rule02.logic.RuleLogic201;
import io.github.quiethappiness.wrench.test.design.framework.biz.rule02.logic.RuleLogic202;
import io.github.quiethappiness.wrench.test.design.framework.biz.rule02.logic.XxxResponse;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @author quiethappiness @jignyue
 * @description
 * @create 2025-01-18 09:19
 */
@Service
public class Rule02TradeRuleFactory {

    @Bean("demo01")
    public BranchLinkedList<String, DynamicContext, XxxResponse> demo01(RuleLogic201 ruleLogic201, RuleLogic202 ruleLogic202) {

        BranchLinkArmory<String, DynamicContext, XxxResponse> BranchLinkArmory = new BranchLinkArmory<>("demo01", ruleLogic201, ruleLogic202);

        return BranchLinkArmory.getLogicLink();
    }

    @Bean("demo02")
    public BranchLinkedList<String, DynamicContext, XxxResponse> demo02(RuleLogic202 ruleLogic202) {

        BranchLinkArmory<String, DynamicContext, XxxResponse> BranchLinkArmory = new BranchLinkArmory<>("demo02", ruleLogic202);

        return BranchLinkArmory.getLogicLink();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends AbstractDynamicContext
    {
        private String age;
    }

}