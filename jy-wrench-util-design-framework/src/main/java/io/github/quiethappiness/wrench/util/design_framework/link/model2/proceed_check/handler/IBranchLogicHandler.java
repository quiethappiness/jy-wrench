package io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check.handler;

import io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check.AbstractDynamicContext;

/**
 * @author quiethappiness @jignyue
 * @description 逻辑处理器
 * @create 2025-01-18 09:43
 */
public interface IBranchLogicHandler<T, D extends AbstractDynamicContext, R> {

    default R next(T requestParameter, D dynamicContext) {
        dynamicContext.setProceed(true);
        return null;
    }

    default R stop(T requestParameter, D dynamicContext, R result){
        dynamicContext.setProceed(false);
        return result;
    }

    R apply(T requestParameter, D dynamicContext) throws Exception;

}