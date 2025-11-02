package io.github.quiethappiness.wrencher.test.design.framework.biz.tree.node;

import com.alibaba.fastjson.JSON;
import io.github.quiethappiness.wrencher.test.design.framework.biz.tree.AbstractXxxSupport;
import io.github.quiethappiness.wrencher.test.design.framework.biz.tree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberLevel2Node extends AbstractXxxSupport
{

    @Override
    protected String doApply(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("【级别节点-2】规则决策树 actualValue:{}", requestParameter);
        return "level2" + JSON.toJSONString(dynamicContext);
    }
}