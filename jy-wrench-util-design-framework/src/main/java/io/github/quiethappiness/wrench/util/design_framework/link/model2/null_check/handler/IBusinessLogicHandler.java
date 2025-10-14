package io.github.quiethappiness.wrench.util.design_framework.link.model2.null_check.handler;

/**
 * @author quiethappiness @jingyue
 * @description 逻辑处理器
 * @create 2025-01-18 09:43
 */
public interface IBusinessLogicHandler<T, D, R> {

    default R next(T requestParameter, D dynamicContext) {
        return null;
    }

    R apply(T requestParameter, D dynamicContext) throws Throwable;

}