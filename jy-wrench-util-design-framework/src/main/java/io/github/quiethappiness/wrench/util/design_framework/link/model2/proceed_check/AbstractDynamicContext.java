package io.github.quiethappiness.wrench.util.design_framework.link.model2.proceed_check;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 链路动态上下文
 *
 * @author quiethappiness @jignyue
 * 2025/7/12 16:34
 */
public abstract class AbstractDynamicContext
{

    @Setter
    @Getter private boolean proceed;

    public AbstractDynamicContext() {
        this.proceed = true;
    }

    private final Map<String, Object> dataObjects = new HashMap<>();

    public <T> void setValue(String key, T value) {
        dataObjects.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) dataObjects.get(key);
    }
}