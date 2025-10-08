package io.github.quiethappiness.wrencher.test.design.framework;

import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrencher.test.design.framework.biz.tree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class DesignFrameworkTest {

    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    @Test
    public void test() throws Throwable
    {
        StrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> strategyHandler = defaultStrategyFactory.strategyHandler();
        String result = strategyHandler.apply("xiaofuge", new DefaultStrategyFactory.DynamicContext());

        log.info("测试结果:{}", result);
    }

}